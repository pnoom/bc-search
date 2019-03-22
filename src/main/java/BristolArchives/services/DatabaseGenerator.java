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
import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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

    */

    // TODO: pre-populate the dictionaries first by queriying DB for existing depts and colls
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
            } catch (Exception exception) {
                System.out.println("Skipped Dept: duplicate or malformed entry");
                System.out.println(exception.toString());
                failedDeptAttempts++;
            }
        }
        if (collectionsAdded.get(collName) == null && deptsAdded.get(deptName) != null) {
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

    // Creates an Item and adds it to the buffer (destructively)
    private boolean processItems(Map<String, String> row, List<Item> itemBuffer, Integer bufSize,
                              Map<String, Dept> deptsAdded, Map<String, Collection> collectionsAdded,
                              Map<String, List<String>> allIrns, Map<String, Integer> mediaCounts) {
        boolean batchAdded = false;
        if (row == null) return batchAdded;
        // Create list of Entities, then batch-insert using repo.saveAll(). Would be nice to be able to skip indiviual malformed
        // Items, but difficult with batch saving. Instead, skip the entire batch containing the malformed entry.

        // TODO: provide info about what is malformed, and how
        if (itemBuffer.size() == bufSize) {
            try {
                itemRepo.saveAll(itemBuffer);
            } catch (Exception exception) {
                System.out.println("Skipped batch: contained duplicate or malformed item(s), or Dept/Collection missing");
                System.out.println(exception.toString());
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

        if (row.get(displayDateHeadingArchive) != null) {
            item.setDisplayDate(row.get(displayDateHeadingArchive));
        } else if (row.get(displayDateHeadingMuseum) != null) {
            item.setDisplayDate(row.get(displayDateHeadingMuseum));
        }

        // Needs normalization

        DateMatcher dateMatcher = new DateMatcher();
        dateMatcher.matchAttempt(item.getDisplayDate());

        //item.setStartDate();
        //item.setEndDate();

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

    public void generateDatabase(File dataFile, File mediaFile) throws IOException {
        CSVReaderHeaderAware rowReader;
        Map<String, String> row;
        int fileSize = 0;
        Map<String, Dept> deptsAdded = new HashMap<>();
        Map<String, Collection> collectionsAdded = new HashMap<>();
        Map<String, List<String>> allIrns = new HashMap<>();
        Map<String, Integer> mediaCounts = new HashMap<>();
        List<Item> itemBuffer = new ArrayList<>();

        // Go through file once, adding all necessary Depts and Collections individually, in right order.
        // Accumulate mappings from deptNames to Depts, and collNames to Collections, for use in second pass.
        // Don't need to use ids explicitly since the Java program understands the schema.

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

        System.out.println(fullBatches);
        System.out.println(lastBatchSize);

        // Go through multimedia file, storing mappings from object numbers to a list of media IRNs and the number
        // of media things per object

        // TODO: check that the last line is processed
        rowReader = getCSVReader(mediaFile);
        row = getRow(rowReader);
        while (row != null) {
            processMedia(row, allIrns, mediaCounts);
            row = getRow(rowReader);
        }
        System.out.println("All media irns calculated.");

        // Go through first file again, adding Items in batches to reduce memory usage and SQL processing times

        // TODO: fix s.t. last line is processed
        rowReader = getCSVReader(dataFile);
        do {
            row = getRow(rowReader);
            if (batchesAdded == 91) {
                if (row != null) {
                    System.out.println(row.get(itemRefHeading));
                }
            }
            if (batchesAdded == fullBatches) {
                batchAdded = processItems(row, itemBuffer, lastBatchSize - 1, deptsAdded, collectionsAdded, allIrns, mediaCounts);
            } else {
                batchAdded = processItems(row, itemBuffer, bufferSize, deptsAdded, collectionsAdded, allIrns, mediaCounts);
            }
            if (batchAdded) {
                batchesAdded++;
                System.out.println(batchesAdded);
            }
        } while (row != null);
        System.out.println("All items added.");

    }

    class DateMatcher {
        Date startDate;
        Date endDate;

        public void matchAttempt(String displayDate) {

            String D = "(?:rd|st|nd|th)?";
            String C = "\\[?([a-zA-Z]{2,10})\\]?\\s*(\\d{2,4})\\s*";
            String A = "(\\d{1,2})" +D+ "\\s*[-]?\\s*([a-zA-Z]{2,10})\\s*[-]?\\s*(\\d{2,4})\\s*";
            String B = "\\s*(c.)?\\s*(\\d{4})(s)?\\s*";

            List<String> patternStrings = new ArrayList<>(Arrays.asList("(\\d{1,2})"+D+"\\s*[-]?\\s*([a-zA-Z]{2,10})\\s*[-]?\\s*(\\d{2,4})\\s*[-]?\\s*"+A
                    ,"(\\d{1,2})\\s*[-]?\\s*([a-zA-Z]{2,10})\\s*[-]?\\s*"+A
                    ,A
                    ,"(\\d{0,2})-(\\d{1,2})\\s*[-]?\\s*(\\w{2,10})\\s*[-]?\\s*(\\d{2,4})\\s*"
                    ,C+"\\s*[-]?\\s*"+C
                    ,C+"\\s*[-]?\\s*"+B
                    ,C
                    ,"\\[?([a-zA-Z]{2,10})\\]?\\s*[-]?\\s*(\\d{2})\\s*"
                    ,"\\[?"+B+"-"+B+"\\]?"
                    ,"\\[?"+B+"\\]?"
                    ,"(?:[a-zA-Z]+,\\s*)"+A));

            List<Pattern> patterns = patternStrings.stream().map(x -> Pattern.compile(x)).collect(Collectors.toList());

            List<Consumer<Matcher>> handlers = new ArrayList<>(Arrays.asList(handler0,
                    handler1, handler2, handler3, handler4, handler5, handler6, handler7, handler8, handler9, handler10));

            for (int i=0; i<patterns.size(); i++) {
                Matcher matcher = patterns.get(i).matcher(displayDate);
                boolean matches = matcher.matches();
                // System.out.println(matches);
                if (matches) {
                     handlers.get(i).accept(matcher);
                }
            }
        }

        private Date formatDDMonthYY(String day, String mon, String year) {
            Integer month = monthToNumber(mon);
            DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
            System.out.printf("%4s-%2s-%2s\n", year, month, day);
            try {

                return df.parse(String.format("%4s-%2s-%2s", year, month, day));
            } catch (ParseException exception) {
                return null;
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
            DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
            System.out.printf("%s %s\n", df.format(startDate), df.format(endDate));
        };

        //
        Consumer<Matcher> handler1  = matcher -> {
            //startDate = ;
            //endDate = ;
        };

        Consumer<Matcher> handler2  = matcher -> {
            //startDate = ;
            //endDate = ;
        };

        Consumer<Matcher> handler3  = matcher -> {
            //startDate = ;
            //endDate = ;
        };

        Consumer<Matcher> handler4  = matcher -> {
            //startDate = ;
            //endDate = ;
        };

        Consumer<Matcher> handler5  = matcher -> {
            //startDate = ;
            //endDate = ;
        };

        Consumer<Matcher> handler6  = matcher -> {
            //startDate = ;
            //endDate = ;
        };

        Consumer<Matcher> handler7  = matcher -> {
            //startDate = ;
            //endDate = ;
        };

        Consumer<Matcher> handler8  = matcher -> {
            //startDate = ;
            //endDate = ;
        };

        Consumer<Matcher> handler9  = matcher -> {
            //startDate = ;
            //endDate = ;
        };

        Consumer<Matcher> handler10  = matcher -> {
            //startDate = ;
            //endDate = ;
        };
    }
}

