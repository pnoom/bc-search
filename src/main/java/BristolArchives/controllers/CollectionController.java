package BristolArchives.controllers;

import BristolArchives.entities.Collection;
import BristolArchives.services.CollectionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
//@RequestMapping("/Collections")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;


    public ModelAndView getAllCollections() {
        System.out.println("Try to find collection page!!!!!");
        ModelAndView mv = new ModelAndView();
        mv.addObject("collectionList", collectionService.getAllCollections());
        mv.setViewName("allCollections");
        return mv;
    }

    @GetMapping("/allCollections")
    public String ListCollections(ModelMap model)
    {
        model.addAttribute("result", collectionService.getAllCollections());
        return "allCollections";
    }

    @ResponseBody
    @RequestMapping("/showLists")
    public List<Collection> getCollList() {
        return collectionService.getAllCollections();
    }
}
