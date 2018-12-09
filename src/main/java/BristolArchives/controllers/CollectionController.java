package BristolArchives.controllers;

import BristolArchives.entities.Collection;
import BristolArchives.repositories.CollectionRepo;
import BristolArchives.services.CollectionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;


@Controller
//@RequestMapping("/Collections")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @GetMapping("/allCollections")
    public String ListCollections(ModelMap model) {
        model.addAttribute("collectionList", collectionService.getAllCollections());
        return "allCollections";
    }

    @ResponseBody
    @RequestMapping("/showLists")
    public List<Collection> getCollList() {
        return collectionService.getAllCollections();
    }




    @RequestMapping(value="/results", method=RequestMethod.POST)
    public String displayResult(@RequestParam(value = "main_search", required = false) String search ,Model model){
        if(search == null) {
            model.addAttribute("collectionsResults", collectionService.getAllCollections());
        }
        else{
            model.addAttribute("collectionsResults", collectionService.getByNameContaining(search));
            }

        return "result";
    }

}
