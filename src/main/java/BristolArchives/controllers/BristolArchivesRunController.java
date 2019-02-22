package BristolArchives.controllers;

import BristolArchives.services.DatabaseGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;


@Controller
public class BristolArchivesRunController {
    @GetMapping("/")
    public String index() {
        File logFile = new File("/home/andy/studies/spe/bc-search/src/main/resources/public/db-gen-log.txt");
        PrintWriter logWriter;
        try {
            logWriter = new PrintWriter(logFile);
        } catch (FileNotFoundException exception) {
            System.out.println("LOG FILE NOT FOUND");
            return null;
        }
        logWriter.println("Start of log file:");
        DatabaseGenerator dbGen = new DatabaseGenerator();
        File file = dbGen.getFile("haslam.csv");
        logWriter.println(file.getAbsolutePath());
        dbGen.generateDatabase(file, logWriter);
        logWriter.flush();
        logWriter.close();
        return "index";
    }
}
