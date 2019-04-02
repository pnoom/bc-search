package BristolArchives.controllers;

import BristolArchives.services.DatabaseGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;


@Controller
public class BristolArchivesRunController {
    @Autowired
    DatabaseGenerator dbGen;

    @GetMapping("/")
    public String index() {

        // Uncomment this to delete all database content. USE WITH CAUTION.
        // dbGen.deleteEntireDatabase();

        // Uncomment this to generate the DB. Need to drop tables first (run "source collections.sql")


        File dataFile = null;
        File mediaFile = null;
        try {
            dataFile = dbGen.getFile("emu-content2.csv");
            //dataFile = dbGen.getFile("reduced-size-emu.csv");
            //mediaFile = dbGen.getFile("emu-content-mm2.csv");
        } catch (IOException exception) {
            System.out.println("Error opening CSV file");
        }
        try {
            dbGen.generateDatabase(dataFile, mediaFile);
            //dbGen.deleteSpecifiedItems(dataFile);
        } catch (IOException exception) {
            System.out.println("Could not generate database");
            System.out.println(exception.toString());
        }


        return "index";
    }
}
