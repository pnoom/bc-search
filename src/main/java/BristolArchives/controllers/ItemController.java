package BristolArchives.controllers;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class ItemController {
    @PostMapping("/results")
    public String sendResult(@RequestParam(value = "main_search", required = false) String search){
        return "redirect:/results/" + search;
    }

    @GetMapping("/results/")
    public String emptySearch(Model model){
        model.addAttribute("collectionsResults", itemService.getAllCollections());
        return "redirect:/";
    }

    @GetMapping(value="/results/{search}")
    public String displayResult(@PathVariable String search , Model model){
        if(search == null) {
            model.addAttribute("collectionsResults", itemService.getAllCollections());
        }
        else{
            model.addAttribute("collectionsResults", collectionService.getByNameContaining(search));
        }

        return "result";
    }

}

