package BristolArchives.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class aboutArchivesController {
    @GetMapping("/aboutArchives")
    public String aboutArchives(){
        return "aboutArchives";
    }
    @GetMapping("/loginPage")
    public String loginPage(){
        return "loginPage";
    }
    @GetMapping("/submitCsv")
    public String submitCsv(){
        return "submitCsv";
    }
}

