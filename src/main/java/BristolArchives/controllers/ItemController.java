package BristolArchives.controllers;

import BristolArchives.services.CollectionService;
import BristolArchives.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/itemSearch")
        public String ListCollections(ModelMap model) {
        model.addAttribute("itemList", itemService.getItem("Lamu well"));

        return "itemResults";
    }
}
