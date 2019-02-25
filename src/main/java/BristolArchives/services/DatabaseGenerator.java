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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.ArrayList;
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

    public File getFile(String filename) {
        Resource resource = new ClassPathResource("public/" + filename);
        try {
            File file = resource.getFile();
            return file;
        } catch (IOException exception) {
            return null;
        }
    }

    // Start reading the file from the start
    private CSVReaderHeaderAware getCSVReader(File file, PrintWriter logWriter) {
        FileReader fileReader;
        CSVReaderHeaderAware rowReader;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException exception) {
            System.out.println("CSV FILE NOT FOUND");
            logWriter.println("CSV FILE NOT FOUND");
            return null;
        }
        try {
            rowReader = new CSVReaderHeaderAware(fileReader);
        } catch (IOException exception) {
            System.out.println("IO ERROR ON CSV READ");
            logWriter.println("IO ERROR ON CSV READ");
            return null;
        }
        return rowReader;
    }

    // Read a row into a Map with column headings as keys and row entries as values
    private Map<String, String> getRow(CSVReaderHeaderAware rowReader, PrintWriter logWriter) {
        Map<String, String> row;
        try {
            row = rowReader.readMap();
        } catch (IOException exception) {
            System.out.println("IO ERROR ON CSV READ");
            logWriter.println("IO ERROR ON CSV READ");
            return null;
        }
        return row;
    }

    // This may actually need to return something
    private void processDeptsAndCollections(Map<String, String> row, List<String> deptsAdded, List<String> collectionsAdded) {
        // Create list of Entities, then batch-insert using repo.saveAll()
        // But can't really batch-insert Items if we don't know that all their Depts and Collections already exist, right?
        // Only know that once we've gone through the whole file, but if we accumulate Items as we go, we'll have 75000 of
        // them in memory by then, right? So HAVE to go through the file twice. Save the list of Map objects instead? Any
        // memory gain?

        return;
    }

    // Creates an Item and adds it to the buffer (destructively)
    private void processItems(Map<String, String> row, List<Item> itemBuffer) {

    }

    public void generateDatabase(File file) {
        // Open a log file
        File logFile = getFile("db-gen-log.txt");
        PrintWriter logWriter;
        try {
            logWriter = new PrintWriter(logFile);
        } catch (FileNotFoundException exception) {
            System.out.println("LOG FILE NOT FOUND");
            return;
        }
        logWriter.println("Start of log file:");

        // Just an example to check it's working
        //System.out.println(row.get("Object Number"));
        //logWriter.println(row.get("Object Number"));

        // Go through file once, adding all necessary Depts and Collections individually, in right order
        List<String> deptsAdded = new ArrayList<>();
        List<String> collectionsAdded = new ArrayList<>();
        CSVReaderHeaderAware rowReader = getCSVReader(file, logWriter);
        Map<String, String> row = getRow(rowReader, logWriter);
        while (row != null) {
            processDeptsAndCollections(row, deptsAdded, collectionsAdded);
            row = getRow(rowReader, logWriter);
        }
        System.out.println("All depts and collections added.");
        logWriter.println("All rows processed.");

        // Go through file again, adding Items in batches to reduce memory usage and SQL processing times
        rowReader = getCSVReader(file, logWriter);
        row = getRow(rowReader, logWriter);
        List<Item> itemBuffer = new ArrayList<>();
        while (row != null) {
            processItems(row, itemBuffer);
            row = getRow(rowReader, logWriter);
        }

        System.out.println("All items added.");
        logWriter.println("All items added.");
        logWriter.flush();
        logWriter.close();
    }
}
