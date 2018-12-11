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

    @PostMapping("/search")
    public String sendResult(@RequestParam(value = "main_search", required = false) String search){
        return "redirect:/search?q=" + search;
    }

//    @GetMapping("/item-results/")
//    public String emptySearch(){
//        return "redirect:/";
//    }

    @GetMapping("/search")
    public String displayResult(@RequestParam String q , Model model){
        if(q == null) {
            //model.addAttribute("collectionsResults", itemService.getItem(search));
            return "redirect:/";
        }
        else{
            model.addAttribute("itemList", itemService.getItem(q));
        }
        return "itemResults";
    }

    @GetMapping("/items/{itemRef}")
    public String displaySingleItem(@PathVariable String itemRef, Model model) {
        itemRef = itemRef.replace('-','/');

        if(itemRef == null) {
            //model.addAttribute("collectionsResults", itemService.getItem(search));
            return "redirect:/";
        }
        else{
            model.addAttribute("item", itemService.getExactItem(itemRef));
        }
        return "itemPage";
    }

}


