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
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
        return WordUtils.capitalizeFully(string.trim().toLowerCase().replaceAll(" +", " "));
    }

    // This may actually need to return something
    private void processDeptsAndCollections(Map<String, String> row, Map<String, Dept> deptsAdded,
                                            Map<String, Collection> collectionsAdded) {
        String deptName = sanitizeString(row.get("Department"));
        String collName = sanitizeString(row.get("Collection"));
        Dept dept;
        Collection coll;
        if (deptsAdded.get(deptName) == null) {
            dept = new Dept();
            // id will be auto-generated by save(), right?
            //dept.setId(deptsAdded.size());
            dept.setName(deptName);
            deptRepo.save(dept); // UNCOMMENT ME
            deptsAdded.put(deptName, dept);
            System.out.printf("Department: '%s'\n", deptName);
        }
        // Its dept is guaranteed to exist at this point, so get its id from deptsAdded dict
        if (collectionsAdded.get(collName) == null) {
            coll = new Collection();
            // id will be auto-generated by save, right?
            //coll.setId(collectionsAdded.size());
            coll.setName(collName);
            coll.setDept(deptsAdded.get(deptName));
            //collectionRepo.save(coll); // UNCOMMENT ME
            collectionsAdded.put(collName, coll);
            System.out.printf("Collection: '%s'\n", collName);
        }
    }

    // Creates an Item and adds it to the buffer (destructively)
    private void processItems(Map<String, String> row, List<Item> itemBuffer, Integer bufSize,
                              Map<String, Dept> deptsAdded, Map<String, Collection> collectionsAdded) {
        // Create list of Entities, then batch-insert using repo.saveAll()
        if (itemBuffer.size() == bufSize) {
            //itemRepo.saveAll(itemBuffer); // UNCOMMENT ME
            System.out.println("Batch insert");
            itemBuffer.clear();
        }

        // Some of these values are missing, and/or their column is dependent on whether it's a museum
        // or archive item, so use placeholders for now to check other code
        Item item = new Item();
        // auto-generated?
        // item.setId()
        item.setCollection(collectionsAdded.get(sanitizeString(row.get("Collection"))));
        item.setItemRef(row.get("Object Number"));
        item.setLocation("placeholder");
        item.setName(row.get("Full Name"));
        // can be null, so leave as null for now
        //item.setDescription("placeholder");
        //item.setDisplayDate("placeholder");
        //item.setStartDate();
        //item.setEndDate();
        //item.setCopyrighted();
        //item.setExtent();
        //item.setPhysTechDesc();
        //item.setMultimediaIrn();  // will have to combine the IRNs with the main spreadsheet somehow
        item.setCollectionDisplayName(row.get("Named Collection"));
        itemBuffer.add(item);
    }

    public void generateDatabase(File file) throws IOException {
        CSVReaderHeaderAware rowReader;
        Map<String, String> row;

        Map<String, Dept> deptsAdded = new HashMap<>();
        Map<String, Collection> collectionsAdded = new HashMap<>();
        List<Item> itemBuffer = new ArrayList<>();

        // Go through file once, adding all necessary Depts and Collections individually, in right order.
        // Accumulate mappings from deptNames to Depts, and collNames to Collections, for use in second pass.
        // Don't need to use ids explicitly since the Java program understands the schema.
        rowReader = getCSVReader(file);
        row = getRow(rowReader);
        while (row != null) {
            processDeptsAndCollections(row, deptsAdded, collectionsAdded);
            row = getRow(rowReader);
        }
        System.out.println("All depts and collections added.");

        // Go through file again, adding Items in batches to reduce memory usage and SQL processing times
        rowReader = getCSVReader(file);
        row = getRow(rowReader);
        while (row != null) {
            processItems(row, itemBuffer, bufferSize, deptsAdded, collectionsAdded);
            row = getRow(rowReader);
        }
        System.out.println("All items added.");
    }
}
