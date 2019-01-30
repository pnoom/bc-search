package BristolArchives.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class BristolArchivesRunController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/advanceSearch")
    public String advanceSearch() {
        return "advanceSearch";
    }
}