package BristolArchives.controllers;

import BristolArchives.services.CollectionService;
import BristolArchives.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping("/item-results")
    public String sendResult(@RequestParam(value = "main_search", required = false) String search){
        return "redirect:/item-results/" + search;
    }

    @GetMapping("/item-results/")
    public String emptySearch(){
        return "redirect:/";
    }

    @GetMapping(value="/item-results/{search}")
    public String displayResult(@PathVariable String search , Model model){
        if(search == null) {
            //model.addAttribute("collectionsResults", itemService.getItem(search));
            return "redirect:/";
        }
        else{
            model.addAttribute("itemList", itemService.getItem(search));
        }

        return "itemResults";
    }

}


