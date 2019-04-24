package BristolArchives.services;

/*
This class should load up a CSV file with a clearly-specified set of columns
containing the required data fields about archive/museum items. It should go
through them creating entity instances (Dept, Collection and Item) and actually
insert them into the MySQL database that the program is connected to. It may have
to do so in batches, firstly to limit memory usage, and secondly to minimize the
number of SQL queries necessary, thereby increasing performance.

This class should therefore depend on the Repo classes, a CSV-reading library, and
a regex library.

The set of data fields needed for an Item is as follows:

Object number
Location (of provenance/production)
Name
Description
Date (we will infer/extract start_date and end_date)
Copyright status/holder
Extent
Phys/Tech/Desc
Multimedia IRN
Collection display name (e.g. Trotter)
Collection id (just an SQL auto-generated integer: a foreign key into collection table)

For a Collection, we just need its name and its Dept id (again a integer foreign key).
For a Dept, we just need its name.

In order to insert an Item, we first need to insert its Collection, and to do that,
we first need to insert that Collection's Dept. So the order is Dept, Collection, Item.
Need to keep track of those that have already been added.

 */

import BristolArchives.entities.Collection;
import BristolArchives.entities.Dept;
import BristolArchives.entities.Item;
import BristolArchives.repositories.CollectionRepo;
import BristolArchives.repositories.DeptRepo;
import BristolArchives.repositories.ItemRepo;
import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVParser;
import com.opencsv.ICSVWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedRuntimeException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DatabaseGenerator {
    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private CollectionRepo collectionRepo;

    @Autowired
    private DeptRepo deptRepo;

    private Integer bufferSize = 1000;
    private Integer maxDeptAttempts = 10;
    private Integer maxCollAttempts = 10;
    private Integer failedDeptAttempts = 0;
    private Integer failedCollAttempts = 0;

    private String departmentHeading = "Department: ";
    private String collectionHeading = "Collection";
    private String locationHeadingArchive = "Geographic Name: (Names)";
    private String locationHeadingMuseum = "Place Details: (Production Place)";
    private String nameHeading = "Full Name: (Collection Details)";
    private String itemRefHeading = "Object Number";
    private String descriptionHeadingArchive = "Scope and Content: (Archival Description)/Scope and Content: (Content and Structure)";
    private String descriptionHeadingMuseum = "Physical Description: (Collection Details)";
    private String displayDateHeadingArchive = "Unit Date: (Unit Details)/Date(s): (ISAD(G) Identity Statement)";
    private String displayDateHeadingMuseum = "Associated Date: (Content Details)/Production Date: (Production Dates)";
    private String extentHeadingArchive = "Extent: (ISAD(G) Identity Statement)";
    private String extentHeadingMuseum = "Simple Name: (Collection Details)/Object Name/Object Name: (Classification)";
    private String collectionDisplayNameHeading = "Named Collection";
    private String multimediaIrnHeading = "Multimedia Irn";
    private String copyrightHeading = "Copyright";

    public File getFile(String filename) throws IOException{
        Resource resource = new ClassPathResource("public/" + filename);
        File file;
        try {
            file = resource.getFile();
            return file;
        } catch (IOException exception) {
            System.out.printf("FILE '%s' NOT FOUND\n", filename);
            throw exception;
        }
    }

    // Start reading the file from the start
    private CSVReaderHeaderAware getCSVReader(File file)
            throws IOException {
        FileReader fileReader;
        CSVReaderHeaderAware rowReader;
        try {
            fileReader = new FileReader(file);
            try {
                rowReader = new CSVReaderHeaderAware(fileReader);
            } catch (IOException exception) {
                System.out.println("IO ERROR ON CSV READ");
                throw exception;
            }
        } catch (FileNotFoundException exception) {
            System.out.println("CSV FILE NOT FOUND");
            throw exception;
        }
        return rowReader;
    }

    // Read a row into a Map with column headings as keys and row entries as values
    private Map<String, String> getRow(CSVReaderHeaderAware rowReader)
            throws IOException {
        return rowReader.readMap();
    }

    private String sanitizeString(String string) {
        if (string == null) {
            return "";
        }
        string = WordUtils.capitalizeFully(string.trim().toLowerCase().replaceAll(" +", " "));
        return string;
    }

    private String truncateString(String string, Integer maxLength) {
        if (string == null) {
            return "";
        }
        string = string.trim();
        return string.substring(0, Math.min(maxLength-1, string.length()));
    }

    /*
    Possible exceptions?
    Inner
    org.hibernate.exception.ConstraintViolationException
    Outer
    org.springframework.dao.DataIntegrityConstraintViolationException
    Inner
    java.lang.IllegalStateException
    Outer
    org.springframework.transaction.CannotCreateTransactionException

    I think the parent of the ones I want is:
    org.springframework.dao.DataAccessException
    The parent of that is:
    org.springframework.core.NestedRuntimeException
    */

    private void processDeptsAndCollections(Map<String, String> row, Map<String, Dept> deptsAdded,
                                            Map<String, Collection> collectionsAdded) {
        String deptName = sanitizeString(row.get(departmentHeading));
        String collName = sanitizeString(row.get(collectionHeading));
        //if (deptName.isEmpty()) {
        //    // Don't add anything if the department field was empty
        //    return;
        //}
        Dept dept;
        Collection coll;

        if (failedDeptAttempts >= maxDeptAttempts) {
            throw new RuntimeException("Aborting: too many failed attempts to add Departments");
        }
        if (failedCollAttempts >= maxCollAttempts) {
            throw new RuntimeException("Aborting: too many failed attempts to add Collections");
        }
        //System.out.println(deptsAdded.get(deptName));
        if (deptsAdded.get(deptName) == null) {
            dept = new Dept();
            // id is auto-generated
            dept.setName(deptName);
            try {
                deptRepo.saveAndFlush(dept);
                deptsAdded.put(deptName, dept);
            }
            catch (Exception exception) {
                System.out.println("Skipped Dept: duplicate or malformed entry");
                System.out.println(exception.toString());
                failedDeptAttempts++;
            }
        }
        // How to handle null deptsAdded.get(deptName)?
        if (collectionsAdded.get(collName) == null) {
            coll = new Collection();
            // id is auto-generated
            coll.setName(collName);
            coll.setDept(deptsAdded.get(deptName));
            try {
                collectionRepo.saveAndFlush(coll);
                collectionsAdded.put(collName, coll);
            } catch (Exception exception) {
                System.out.println("Skipped Collection: duplicate or malformed entry, or missing Dept");
                System.out.println(exception.toString());
                failedCollAttempts++;
            }
        }
    }

    private String stringifyIrns(List<String> allIrns) {
        // System.out.println(allIrns);
        return String.join(",", allIrns);
    }

    private String extractUsefulErrorMessage(NestedRuntimeException exception) {
        return exception.getMostSpecificCause().getMessage();
    }

    private String extractDuplicateItemRef(String duplicateErrorMessage) {
        String result = duplicateErrorMessage.substring(duplicateErrorMessage.indexOf('\'') + 1);
        result = result.substring(0, result.indexOf('\''));
        return result;
    }

    // Creates an Item and adds it to the buffer (destructively)
    private boolean processItems(Map<String, String> row, List<Item> itemBuffer, Integer bufSize,
                              Map<String, Dept> deptsAdded, Map<String, Collection> collectionsAdded,
                              Map<String, List<String>> allIrns, Map<String, Integer> mediaCounts) {
        boolean batchAdded = false;
        if (row == null) return batchAdded;

        // Create list of Entities, then batch-insert using repo.saveAll(). Would be nice to be able to skip indiviual malformed
        // Items, but difficult with batch saving. Instead, skip the entire batch containing the malformed entry.

        // TODO: maybe handle blank lines and malformed items/dates more gracefully. Can a required Dept/Collection actually be missing?
        // That shouldn't be the case unless there were some exceptions earlier on.
        if (itemBuffer.size() == bufSize) {
            try {
                itemRepo.saveAll(itemBuffer);
            }
            catch (DataIntegrityViolationException exception) {
                System.out.printf("Skipped batch: contained duplicate item(s), starting with the item with reference number '%s'\n",
                        extractDuplicateItemRef(extractUsefulErrorMessage(exception)));
                //System.out.println(extractUsefulErrorMessage(exception));
            }
            catch (NestedRuntimeException exception) {
                System.out.println("Skipped batch: contained malformed item(s), blank line, or a required Dept/Collection was missing. Details:");
                System.out.println(extractUsefulErrorMessage(exception));
            } finally {
                itemRepo.flush();
            }
            itemBuffer.clear();
            batchAdded = true;
        }

        // TODO: trim whitespace etc for all values?

        Item item = new Item();
        // id is auto-generated

        // TODO: check for null collection and skip this individual Item (never add it to the buffer, and reduce the
        // total number of items to add (i.e. fullBatches and/or lastBatchSize etc))
        item.setCollection(collectionsAdded.get(sanitizeString(row.get(collectionHeading))));
        item.setItemRef(row.get(itemRefHeading).trim());

        if (row.get(locationHeadingArchive) != null) {
            item.setLocation(row.get(locationHeadingArchive));
        } else if (row.get(locationHeadingMuseum) != null) {
            item.setLocation(row.get(locationHeadingMuseum));
        }

        if (row.get(nameHeading) != null &&
                !truncateString(row.get(nameHeading), 200).isEmpty()) {
            item.setName(truncateString(row.get(nameHeading), 200));
        } else {
            item.setName(row.get(itemRefHeading));
        }

        if (row.get(descriptionHeadingMuseum) != null) {
            item.setDescription(row.get(descriptionHeadingMuseum));
        } else if (row.get(descriptionHeadingArchive) != null) {
            item.setDescription(row.get(descriptionHeadingArchive));
        }

        if (allIrns.get(row.get(itemRefHeading)) != null) {
            item.setMediaIrns(stringifyIrns(allIrns.get(row.get(itemRefHeading))));
        }

        if (mediaCounts.get(row.get(itemRefHeading)) == null) {
            item.setMediaCount(0);
        } else {
            item.setMediaCount(mediaCounts.get(row.get(itemRefHeading)));
        }

        if (row.get(copyrightHeading) !=null) {
            item.setCopyrighted(row.get(copyrightHeading));
        } else {
            item.setCopyrighted("Unknown");
        }


        if (row.get(displayDateHeadingArchive) != null && !StringUtils.isAllBlank(row.get(displayDateHeadingArchive))) {
            item.setDisplayDate(row.get(displayDateHeadingArchive));
        } else if (row.get(displayDateHeadingMuseum) != null && !StringUtils.isAllBlank(row.get(displayDateHeadingMuseum))) {
            item.setDisplayDate(row.get(displayDateHeadingMuseum));
        }

        if (item.getDisplayDate() != null) {
            DateMatcher dateMatcher = new DateMatcher();
            dateMatcher.matchAttempt(item.getDisplayDate());
            item.setStartDate(dateMatcher.startDate);
            item.setEndDate(dateMatcher.endDate);
        }

        if (row.get(extentHeadingArchive) != null) {
            item.setExtent(row.get(extentHeadingArchive));
        } else if (row.get(extentHeadingMuseum) != null) {
            item.setExtent(row.get(extentHeadingMuseum));
        }

        // Missing/already used
        //item.setPhysTechDesc();

        item.setCollectionDisplayName(truncateString(sanitizeString(row.get(collectionDisplayNameHeading)), 200));
        item.setThumbnailIrn();
        itemBuffer.add(item);
        return batchAdded;
    }

    private void processMedia(Map<String, String> row, Map<String, List<String>> allIrns, Map<String, Integer> mediaCounts) {
        String objNum = row.get(itemRefHeading);
        String irn = row.get(multimediaIrnHeading);
        List<String> irnList;
        if (allIrns.get(objNum) == null) {
            irnList = new ArrayList<String>();
            irnList.add(irn);
            allIrns.put(objNum, irnList);
            mediaCounts.put(objNum, 1);
        } else {
            if (!allIrns.get(objNum).contains(irn)) {
                irnList = allIrns.get(objNum);
                irnList.add(irn);
                allIrns.put(objNum, irnList);
            }
            mediaCounts.put(objNum, mediaCounts.get(objNum) + 1);
        }
    }

    public void deleteEntireDatabase() {
        try {
            itemRepo.deleteAllInBatch();
            collectionRepo.deleteAllInBatch();
            deptRepo.deleteAllInBatch();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to delete all database content");
        }
    }

    public void deleteSpecifiedItems(File dataFile) throws IOException {
        CSVReaderHeaderAware rowReader;
        Map<String, String> row;
        List<String> itemRefs = new ArrayList<>();

        // TODO: check it gets the last one too
        rowReader = getCSVReader(dataFile);
        row = getRow(rowReader);
        while (row != null) {
            itemRefs.add(row.get(itemRefHeading)); // this should never be null, right?
            row = getRow(rowReader);
        }

        try {
            itemRepo.deleteAll(itemRepo.findByItemRefIn(itemRefs));
        } catch (NestedRuntimeException exception) {
            System.out.println("Deletion failed. Details:");
            System.out.println(extractUsefulErrorMessage(exception));
        } finally {
            itemRepo.flush();
        }
    }

    private String[] convertItemToStringArray(Item item) {
        String[] result = {};
        return result;
    }

    public void dumpDatabaseToCSV(File outputFile, Integer bufSize) throws IOException {
        FileWriter fileWriter;
        ICSVWriter csvWriter;

        fileWriter = new FileWriter(outputFile);
        csvWriter = new CSVWriterBuilder(fileWriter)
                .withSeparator(ICSVParser.DEFAULT_SEPARATOR)
                .withQuoteChar(ICSVParser.DEFAULT_QUOTE_CHARACTER)
                .withEscapeChar(ICSVParser.DEFAULT_ESCAPE_CHARACTER)
                .withLineEnd(ICSVWriter.DEFAULT_LINE_END)
                .build();

        List<Item> items;
        List<String[]> buffer;
        String[] line;


        if (buffer.size() == bufSize) {
            csvWriter.writeAll(buffer);
            buffer.clear();
        } else {

            for (Item item : items) {
                line = convertItemToStringArray(item);
                buffer.add(line);
            }
        }
    }

    public void generateDatabase(File dataFile, File mediaFile) throws IOException {
        CSVReaderHeaderAware rowReader;
        Map<String, String> row;
        int fileSize = 0;
        Map<String, Dept> deptsAdded = new HashMap<>();
        Map<String, Collection> collectionsAdded = new HashMap<>();
        Map<String, List<String>> allIrns = new HashMap<>();
        Map<String, Integer> mediaCounts = new HashMap<>();
        List<Item> itemBuffer = new ArrayList<>();
        List<Dept> existingDepts;
        List<Collection> existingCollections;

        // Go through file once, adding all necessary Depts and Collections individually, in right order.
        // Accumulate mappings from deptNames to Depts, and collNames to Collections, for use in second pass.
        // Don't need to use ids explicitly since the Java program understands the schema.

        //DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        //DateMatcher dateMatcher = new DateMatcher();
        // DD/MM/YYYY
        //dateMatcher.matchAttempt("12-05-1921");
        //System.out.printf("%s %s\n", df.format(dateMatcher.startDate), df.format(dateMatcher.endDate));


        // Pre-populate the dicts of Depts and Colls with the current contents of the DB
        try {
            existingDepts = deptRepo.findAll();
            existingCollections = collectionRepo.findAll();
        } catch (Exception exception) {
            throw new RuntimeException("Aborting: failed to retrieve existing Depts and/or Collections");
        }
        // Check lists of existing, convert to Map from names to object references
        if (existingDepts != null) {
            for (Dept dept : existingDepts) {
                deptsAdded.put(dept.getName(), dept);
            }
        }
        if (existingCollections != null) {
            for (Collection coll : existingCollections) {
                collectionsAdded.put(coll.getName(), coll);
            }
        }

        // TODO: check that the last line is processed
        rowReader = getCSVReader(dataFile);
        row = getRow(rowReader);
        while (row != null) {
            processDeptsAndCollections(row, deptsAdded, collectionsAdded);
            row = getRow(rowReader);
            fileSize++;
        }
        System.out.println("All depts and collections added.");

        int fullBatches = fileSize / bufferSize;
        int lastBatchSize = fileSize % bufferSize;
        int batchesAdded = 0;
        boolean batchAdded = false;

        //System.out.println(fullBatches);
        //System.out.println(lastBatchSize);

        // Go through multimedia file (if provided) storing mappings from object numbers to a list of media IRNs and
        // the number of media things per object

        if (mediaFile != null) {
            // TODO: check that the last line is processed
            rowReader = getCSVReader(mediaFile);
            row = getRow(rowReader);
            while (row != null) {
                processMedia(row, allIrns, mediaCounts);
                row = getRow(rowReader);
            }
            System.out.println("All media irns calculated.");
        }

        // Go through first file again, adding Items in batches to reduce memory usage and SQL processing times

        // TODO: fix s.t. last line is processed
        rowReader = getCSVReader(dataFile);
        do {
            row = getRow(rowReader);
            //if (batchesAdded == 91) {
            //    if (row != null) {
            //        System.out.println(row.get(itemRefHeading));
            //    }
            //}
            if (batchesAdded == fullBatches) {
                batchAdded = processItems(row, itemBuffer, lastBatchSize - 1, deptsAdded, collectionsAdded, allIrns, mediaCounts);
            } else {
                batchAdded = processItems(row, itemBuffer, bufferSize, deptsAdded, collectionsAdded, allIrns, mediaCounts);
            }
            if (batchAdded) {
                batchesAdded++;
            }
        } while (row != null);
        System.out.println("All items added.");

    }

}
