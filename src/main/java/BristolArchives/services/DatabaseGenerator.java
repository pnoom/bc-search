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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseGenerator {
    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private CollectionRepo collectionRepo;

    @Autowired
    private DeptRepo deptRepo;

    private Integer bufferSize = 1000;

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

    // This may actually need to return something
    private void processDeptsAndCollections(Map<String, String> row, Map<String, Dept> deptsAdded,
                                            Map<String, Collection> collectionsAdded) {
        String deptName = sanitizeString(row.get("Department: "));
        String collName = sanitizeString(row.get("Collection"));
        //if (deptName.isEmpty()) {
        //    // Don't add anything if the department field was empty
        //    return;
        //}
        Dept dept;
        Collection coll;
        //System.out.println(deptsAdded.get(deptName));
        if (deptsAdded.get(deptName) == null) {
            dept = new Dept();
            // id is auto-generated
            dept.setName(deptName);
            deptRepo.saveAndFlush(dept);
            deptsAdded.put(deptName, dept);
        }
        // Its dept is guaranteed to exist at this point, so get it deptsAdded dict
        //System.out.println(collectionsAdded.get(collName));
        if (collectionsAdded.get(collName) == null) {
            coll = new Collection();
            // id is auto-generated
            coll.setName(collName);
            coll.setDept(deptsAdded.get(deptName));
            collectionRepo.saveAndFlush(coll);
            collectionsAdded.put(collName, coll);
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
        // Create list of Entities, then batch-insert using repo.saveAll(). Would be nice to be able to skip malformed
        // Items, but difficult with batch saving.
        if (itemBuffer.size() == bufSize) {
            try {
                itemRepo.saveAll(itemBuffer);
            } catch (Exception exception) {
                System.out.println("Skipped batch: contained malformed item(s)");
                System.out.println(exception.toString());
            } finally {
                itemRepo.flush();
            }

            //System.out.println("Batch insert");
            itemBuffer.clear();
            batchAdded = true;
        }

        // Trim whitespace etc for all values
        //for (Map.Entry<String, String> pair : row.entrySet()) {
        //    pair.getKey()
        //}

        // TODO: Should have a DISTINCT constraint on Object Number. SQL should complain if we try to add an item that
        // already exists. Since we're giving clients access to this code, we should make this check.

        // Some of these values are missing, and/or their column is dependent on whether it's a museum
        // or archive item, so use placeholders for now to check other code
        Item item = new Item();
        // id is auto-generated
        item.setCollection(collectionsAdded.get(sanitizeString(row.get("Collection"))));
        item.setItemRef(row.get("Object Number").trim());

        if (row.get("Geographic Name: (Names)") != null) {
            item.setLocation(row.get("Geographic Name: (Names)"));
        } else if (row.get("Place Details: (Production Place)") != null) {
            item.setLocation(row.get("Place Details: (Production Place)"));
        }

        if (row.get("Full Name") != null) {
            item.setName(truncateString(row.get("Full Name"), 200));
        } else {
            item.setName(row.get("Object Number"));
        }

        if (row.get("Physical Description: (Collection Details)") != null) {
            item.setDescription(row.get("Physical Description: (Collection Details)"));
        } else if (row.get("Scope and Content: (Archival Description)/Scope and Content: (Content and Structure)") != null) {
            item.setDescription(row.get("Scope and Content: (Archival Description)/Scope and Content: (Content and Structure)"));
        }

        if (allIrns.get(row.get("Object Number")) != null) {
            item.setMediaIrns(stringifyIrns(allIrns.get(row.get("Object Number"))));
        }

        if (mediaCounts.get(row.get("Object Number")) == null) {
            item.setMediaCount(0);
        } else {
            item.setMediaCount(mediaCounts.get(row.get("Object Number")));
        }

        // Hard code it for now
        item.setCopyrighted("© Bristol Culture");

        if (row.get("Unit Date: (Unit Details)/Date(s): (ISAD(G) Identity Statement)") != null) {
            item.setDisplayDate(row.get("Unit Date: (Unit Details)/Date(s): (ISAD(G) Identity Statement)"));
        } else if (row.get("Associated Date: (Content Details)/Production Date: (Production Dates)") != null) {
            item.setDisplayDate(row.get("Associated Date: (Content Details)/Production Date: (Production Dates)"));
        }

        // Needs normalization
        //item.setStartDate();
        //item.setEndDate();

        if (row.get("Extent: (ISAD(G) Identity Statement)") != null) {
            item.setExtent(row.get("Extent: (ISAD(G) Identity Statement)"));
        } else if (row.get("Simple Name: (Collection Details)/Object Name/Object Name: (Classification)") != null) {
            item.setExtent(row.get("Simple Name: (Collection Details)/Object Name/Object Name: (Classification)"));
        }

        // Missing/already used
        //item.setPhysTechDesc();

        item.setCollectionDisplayName(truncateString(sanitizeString(row.get("Named Collection")), 200));
        itemBuffer.add(item);
        return batchAdded;
    }

    private void processMedia(Map<String, String> row, Map<String, List<String>> allIrns, Map<String, Integer> mediaCounts) {
        String objNum = row.get("Object Number");
        String irn = row.get("multimedia irn");
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
        rowReader = getCSVReader(mediaFile);
        row = getRow(rowReader);
        while (row != null) {
            processMedia(row, allIrns, mediaCounts);
            row = getRow(rowReader);
        }
        System.out.println("All media irns calculated.");

        // Go through first file again, adding Items in batches to reduce memory usage and SQL processing times
        rowReader = getCSVReader(dataFile);
        do {
            row = getRow(rowReader);
            if (batchesAdded == 91) {
                if (row != null) {
                    System.out.println(row.get("Object Number"));
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
}
