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

        File file = null;
        try {
            file = dbGen.getFile("emu-content2.csv");
        } catch (IOException exception) {
            System.out.println("Error opening CSV file");
        }
        try {
            dbGen.generateDatabase(file);
        } catch (IOException exception) {
            System.out.println("Could not generate database");
        }

        return "index";
    }
}
