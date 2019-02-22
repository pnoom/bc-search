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

import BristolArchives.repositories.CollectionRepo;
import BristolArchives.repositories.DeptRepo;
import BristolArchives.repositories.ItemRepo;
import com.opencsv.CSVReaderHeaderAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
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

    // This may actually need to return something
    private void processRow(Map<String, String> row) {
        return;
    }

    public void generateDatabase(File file) {
        // Open a log file
        File logFile = this.getFile("db-gen-log.txt");
        PrintWriter logWriter;
        try {
            logWriter = new PrintWriter(logFile);
        } catch (FileNotFoundException exception) {
            System.out.println("LOG FILE NOT FOUND");
            return;
        }
        logWriter.println("Start of log file:");

        // Open the CSV file, read column headings to use as keys, and read
        // first line of values into Map under those keys
        FileReader fileReader;
        CSVReaderHeaderAware rowReader;
        Map<String, String> row;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException exception) {
            System.out.println("FILE NOT FOUND");
            logWriter.println("FILE NOT FOUND");
            logWriter.flush();
            logWriter.close();
            return;
        }
        try {
            rowReader = new CSVReaderHeaderAware(fileReader);
            row = rowReader.readMap();
        } catch (IOException exception) {
            System.out.println("IO ERROR ON CSV READ");
            logWriter.println("IO ERROR ON CSV READ");
            logWriter.flush();
            logWriter.close();
            return;
        }

        // Just an example to check it's working
        System.out.println(row.get("Object Number"));
        logWriter.println(row.get("Object Number"));

        // Now process each row in the CSV file
        while (row != null) {
            this.processRow(row);

            try {
                row = rowReader.readMap();
            } catch (IOException exception) {
                System.out.println("IO ERROR ON CSV READ");
                logWriter.println("IO ERROR ON CSV READ");
                logWriter.flush();
                logWriter.close();
                return;
            }
        }

        System.out.println("All rows processed.");
        logWriter.println("All rows processed.");
        logWriter.flush();
        logWriter.close();
    }
}
