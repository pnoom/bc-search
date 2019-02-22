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
        DatabaseGenerator dbGen = new DatabaseGenerator();
        File file = dbGen.getFile("haslam.csv");
        dbGen.generateDatabase(file);
        return "index";
    }
}
