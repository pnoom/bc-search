package BristolArchives.controllers;

import BristolArchives.entities.SubCollection;
import BristolArchives.services.SubCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SubCollectionController {

    @Autowired
    private SubCollectionService subCollectionService;

    @GetMapping("/allSubCollections")
    public String ListCollections(ModelMap model) {
        model.addAttribute("subCollectionList", subCollectionService.getAllSubCollections());
        return "allSubCollections";
    }

//    @ResponseBody
//    @RequestMapping("/showsubLists")
//    public List<SubCollection> getCollList() {
//        return subCollectionService.getAllSubCollections();
//    }
}
