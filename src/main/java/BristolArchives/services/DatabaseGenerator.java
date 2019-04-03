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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedRuntimeException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
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
    private String nameHeading = "Full Name";
    private String itemRefHeading = "Object Number";
    private String descriptionHeadingArchive = "Scope and Content: (Archival Description)/Scope and Content: (Content and Structure)";
    private String descriptionHeadingMuseum = "Physical Description: (Collection Details)";
    private String displayDateHeadingArchive = "Unit Date: (Unit Details)/Date(s): (ISAD(G) Identity Statement)";
    private String displayDateHeadingMuseum = "Associated Date: (Content Details)/Production Date: (Production Dates)";
    private String extentHeadingArchive = "Extent: (ISAD(G) Identity Statement)";
    private String extentHeadingMuseum = "Simple Name: (Collection Details)/Object Name/Object Name: (Classification)";
    private String collectionDisplayNameHeading = "Named Collection";
    private String multimediaIrnHeading = "multimedia irn";

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

        if (row.get(nameHeading) != null) {
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

        // Hard code it for now
        item.setCopyrighted("© Bristol Culture");

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

    class DateMatcher {
        Date startDate;
        Date endDate;

        public void matchAttempt(String displayDate) {

            String D = "(?:rd|st|nd|th)?";
            String C = "\\[?([a-zA-Z]{2,10})\\]?\\s*(\\d{2,4})\\s*"; //Month YYYY
            String A = "(\\d{1,2})" +D+ "\\s*[-]?\\s*([a-zA-Z]{2,10})\\s*[-]?\\s*(\\d{2,4})\\s*"; //DD Month YYYY (-)
            String B = "\\s*(?:n.d.|nd)?\\s*(c.|c|C|C.)?\\s*(\\d{4})(s)?\\s*([\\(]?circa[\\)]?)?\\s*"; //YYYY
            String E = "\\s*(\\d{1,2})(?:.|\\|/)(\\d{1,2})(?:.|\\|/)(\\d{4})\\s*"; //DD/MM/YYYY

            List<String> patternStrings = new ArrayList<>(Arrays.asList("(\\d{1,2})"+D+"\\s*[-]?\\s*([a-zA-Z]{2,10})\\s*[-]?\\s*(\\d{2,4})\\s*[-]?\\s*"+A //DD Month YYYY - DD Month YYYY
                    ,"(\\d{1,2})\\s*[-]?\\s*([a-zA-Z]{2,10})\\s*[-]?\\s*"+A //DD Month - DD Month YYYY
                    ,A //DD Month YYYY
                    ,"(\\d{0,2})-(\\d{1,2})\\s*[-]?\\s*(\\w{2,10})\\s*[-]?\\s*(\\d{2,4})\\s*" //DD-DD Month YYYY
                    ,C+"\\s*[-]?\\s*"+C //Month YYYY - Month YYYY
                    ,C+"\\s*[-]?\\s*"+B //Month YYYY - YYYY
                    ,C //Month YYYY
                    ,"\\[?([a-zA-Z]{2,10})\\]?\\s*[-]?\\s*(\\d{2})\\s*" //Month YY
                    ,"\\[?"+B+"-"+B+"\\]?" // YYYY - YYYY
                    ,"\\[?"+B+"-\\s*(\\d{2})\\s*\\]?" // YYYY - YY
                    ,"\\[?"+B+"\\]?" // YYYY
                    ,"(?:[a-zA-Z]+[,]?\\s*)"+A //Location, DD Month YYYY
                    ,E+"\\s*[-]?\\s*"+E //DD/MM/YYYY - DD/MM/YYYY
                    ,E //DD/MM/YYYY
                    ));

            List<Pattern> patterns = patternStrings.stream().map(x -> Pattern.compile(x)).collect(Collectors.toList());

            List<Consumer<Matcher>> handlers = new ArrayList<>(Arrays.asList(handler0,
                    handler1, handler2, handler3, handler4, handler5, handler6, handler7, handler8, handler9, handler10, handler11,  handler12,  handler13));

            boolean matches = false;
            for (int i=0; i<patterns.size(); i++) {
                Matcher matcher = patterns.get(i).matcher(displayDate);
                matches = matcher.matches();
                if (matches) {
                    handlers.get(i).accept(matcher);
                    break;
                }
            }
            if (!matches) {
                System.out.println(displayDate);
            }
        }

        private Date formatDDMonthYY(String day, String mon, String year) {
            Integer month = monthToNumber(mon);
            DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
            //System.out.printf("%4s-%2s-%2s\n", year, month, day);
            try {
                return df.parse(String.format("%4s-%2s-%2s", year, month, day));
            } catch (ParseException exception) {
                throw new RuntimeException("Parse error");
            }
        }

        private Integer monthToNumber(String month) {
            List<String> names = new ArrayList<>(Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"));
            for (String name : names) {
                if (name.contains(month)) {
                    return Month.valueOf(name.toUpperCase()).getValue();
                }
            }
            return -1;
        }

        // DD Month YYYY - DD Month YYYY
        Consumer<Matcher> handler0  = matcher -> {
            startDate = formatDDMonthYY(matcher.group(1), matcher.group(2), matcher.group(3));
            endDate = formatDDMonthYY(matcher.group(4), matcher.group(5), matcher.group(6));
        };

        // DD Month - DD Month YYYY
        Consumer<Matcher> handler1  = matcher -> {
            startDate = formatDDMonthYY(matcher.group(1), matcher.group(2), matcher.group(5));
            endDate = formatDDMonthYY(matcher.group(3), matcher.group(4), matcher.group(5));
        };

        // DD Month YYYY
        Consumer<Matcher> handler2  = matcher -> {
            startDate = formatDDMonthYY(matcher.group(1), matcher.group(2), matcher.group(3));
            endDate = formatDDMonthYY(matcher.group(1), matcher.group(2), matcher.group(3));
        };

        // DD-DD Month YYYY
        Consumer<Matcher> handler3  = matcher -> {
            startDate = formatDDMonthYY(matcher.group(1), matcher.group(3), matcher.group(4));
            endDate = formatDDMonthYY(matcher.group(2), matcher.group(3), matcher.group(4));
        };

        // Month YYYY - Month YYYY
        Consumer<Matcher> handler4  = matcher -> {
            startDate = formatDDMonthYY("1", matcher.group(1), matcher.group(2));
            endDate = formatDDMonthYY("28", matcher.group(3), matcher.group(4));
        };

        // Month YYYY - YYYY
        Consumer<Matcher> handler5  = matcher -> {
            startDate = formatDDMonthYY("1", matcher.group(1), matcher.group(2));
            endDate = formatDDMonthYY("28", matcher.group(1), matcher.group(4));
        };

        // Month YYYY
        Consumer<Matcher> handler6  = matcher -> {
            startDate = formatDDMonthYY("1", matcher.group(1), matcher.group(2));
            endDate = formatDDMonthYY("28", matcher.group(1), matcher.group(2));
        };

        // Month YY
        Consumer<Matcher> handler7  = matcher -> {
            Integer tempMonth = monthToNumber(matcher.group(1));
            Integer tempYear = Integer.parseInt(matcher.group(2));
            tempYear += 1900;
            DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
            try {
                startDate = df.parse(String.format("%4d-%2d-%2d", tempYear, tempMonth, 1));
                endDate = df.parse(String.format("%4d-%2d-%2d", tempYear, tempMonth, 28));
            } catch (ParseException exception) {
                throw new RuntimeException("Parse error");
            }
        };

        // YYYY - YYYY
        Consumer<Matcher> handler8  = matcher -> {
            DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
            try {
                startDate = df.parse(String.format("%4s-%2d-%2d", matcher.group(2), 1, 1));
                endDate = df.parse(String.format("%4s-%2d-%2d", matcher.group(6), 12, 31));
            } catch (ParseException exception) {
                throw new RuntimeException("Parse error");
            }
        };

        // YYYY-YY
        Consumer<Matcher> handler9  = matcher -> {
            DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
            String centString = matcher.group(2).substring(0,2);
            String decString = matcher.group(5).substring(0,2);
            String concatString = centString.concat(decString);
            try {
                startDate = df.parse(String.format("%4s-%2d-%2d", matcher.group(2), 1, 1));
                endDate = df.parse(String.format("%4s-%2d-%2d", concatString, 12, 31));
            } catch (ParseException exception) {
                throw new RuntimeException("Parse error");
            }
        };

        // YYYY with circa/s inference
        Consumer<Matcher> handler10  = matcher -> {
            DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
            Integer startTempYear = Integer.parseInt(matcher.group(2));
            Integer endTempYear = Integer.parseInt(matcher.group(2));

            if ((matcher.group(1) != null || matcher.group(4) != null) && matcher.group(3) != null) {
                startTempYear -= 10;
                endTempYear += 9;
            } else if (matcher.group(1) != null || matcher.group(4) != null) {
                startTempYear -= 2;
                endTempYear += 2;
            } else if (matcher.group(3) != null) {
                endTempYear += 9;
            }
            try {
                startDate = df.parse(String.format("%4d-%2d-%2d", startTempYear, 1, 1));
                endDate = df.parse(String.format("%4d-%2d-%2d", endTempYear, 12, 31));
            } catch (ParseException exception) {
                throw new RuntimeException("Parse error");
            }
        };

        // Location DD Month YYYY (Same code as handler 2)
        Consumer<Matcher> handler11  = matcher -> {
            startDate = formatDDMonthYY(matcher.group(1), matcher.group(2), matcher.group(3));
            endDate = formatDDMonthYY(matcher.group(1), matcher.group(2), matcher.group(3));
        };

        // DD/MM/YYYY - DD/MM/YYYY
        Consumer<Matcher> handler12  = matcher -> {
            DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
            try {
                startDate = df.parse(String.format("%4s-%2s-%2s", matcher.group(3),matcher.group(2),matcher.group(1)));
                endDate = df.parse(String.format("%4s-%2s-%2s", matcher.group(6),matcher.group(5),matcher.group(4)));
            } catch (ParseException exception) {
                throw new RuntimeException("Parse error");
            }
        };

        // DD/MM/YYYY
        Consumer<Matcher> handler13  = matcher -> {
            DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
            try {
                startDate = df.parse(String.format("%4s-%2s-%2s", matcher.group(3),matcher.group(2),matcher.group(1)));
                endDate = df.parse(String.format("%4s-%2s-%2s", matcher.group(3),matcher.group(2),matcher.group(1)));
            } catch (ParseException exception) {
                throw new RuntimeException("Parse error");
            }
        };
    }
}

