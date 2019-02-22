package BristolArchives.controllers;

import BristolArchives.services.DatabaseGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;


@Controller
public class BristolArchivesRunController {
    @GetMapping("/")
    public String index() {
        // Why is nothing being printed to console???
        System.out.println("\n\nHELLOASKJDHFKAJSDHFLJASDLFJHASKFHLKASDJFHLKAFH\n\n");
        DatabaseGenerator dbGen = new DatabaseGenerator();
        File file = dbGen.getCSVFile("haslam.csv");
        System.out.println(file.getAbsolutePath());
        dbGen.generateDatabase(file);
        System.out.println("\n\nHELLOASKJDHFKAJSDHFLJASDLFJHASKFHLKASDJFHLKAFH\n\n");
        return "index";
    }
}
