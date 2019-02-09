package BristolArchives.controllers;

import BristolArchives.entities.Item;
import BristolArchives.services.CollectionService;
import BristolArchives.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class AdvancedSearchController {
    @Autowired
    private ItemService itemService;
    private CollectionService collectionService;

    private boolean hasSth(String s) {
        return (s != null) && (!s.isEmpty());
    }

    private void getIntersection(List<Item> result, List<Item> newItems, boolean someConstraintsExist) {
        if(result.isEmpty() && !someConstraintsExist)
            result.addAll(newItems);
        else
            result.retainAll(newItems);
    }

    @GetMapping("/advanceSearch")
    public String advanceSearch(Model model) {
        //model.addAttribute("collectionList",collectionService.getAllCollections());
        return "advanceSearch";
    }

    @PostMapping("/advSearch")
    public String sendResult(
            @RequestParam(value = "collection_search", required = false) String adv_coll,
            @RequestParam(value = "date_search", required = false) String adv_date,
            @RequestParam(value = "date_start", required = false) String adv_date_start,
            @RequestParam(value = "date_end", required = false) String adv_date_end,
            @RequestParam(value = "precision_search", required = false) String adv_name,
            @RequestParam(value = "location_search", required = false) String adv_lctn
        ){
        String search = "?";

        if(hasSth(adv_coll))
            search += "&coll=" + adv_coll;
        if(hasSth(adv_date_start))
            search += "&dateStart=" + adv_date_start;
        if(hasSth(adv_date_end))
            search += "&dateEnd=" + adv_date_end;
        if(hasSth(adv_date))
            search += "&date=" + adv_date ;
        if(hasSth(adv_name))
            search += "&name=" + adv_name;
        if(hasSth(adv_lctn))
            search += "&lctn=" + adv_lctn;

        search = search.replaceAll("/","%2F");

        return "redirect:/advSearch" + search;
    }

    @GetMapping("/advSearch")
    public String displayResult(
            @RequestParam(value = "coll", required = false) String adv_coll,
            @RequestParam(value = "date", required = false) String adv_date,
            @RequestParam(value = "dateStart", required = false) Date adv_date_start,
            @RequestParam(value = "dateEnd", required = false) Date adv_date_end,
            @RequestParam(value = "name", required = false) String adv_name,
            @RequestParam(value = "lctn", required = false) String adv_lctn,
            Model model
            ){

        if(!hasSth(adv_coll) && !hasSth(adv_date) && !hasSth(adv_name) && !hasSth(adv_lctn) && (adv_date_start) != null && (adv_date_end) != null)
            return "redirect:/advanceSearch";
        else{
            model.addAttribute("itemList", itemService.getAdvancedSearch(adv_date,adv_date_start,adv_date_end,adv_coll,adv_lctn,adv_name));
            /*
            boolean someConstraintsExist = false;
            List<Item> resultList = new ArrayList();
            if(adv_name != null) {
                getIntersection(resultList, itemService.getItemByName(adv_name), false);
                someConstraintsExist = true;
            }
            if(adv_date != null) {
                getIntersection(resultList,itemService.getItemByDate(adv_date), someConstraintsExist);
                someConstraintsExist = true;
            }
            if(adv_coll != null) {
                getIntersection(resultList, itemService.getItemByCollectionName(adv_coll), someConstraintsExist);
                someConstraintsExist = true;
            }
            if(adv_lctn != null)
                getIntersection(resultList, itemService.getItemByLocation(adv_lctn), someConstraintsExist);
            model.addAttribute("itemList", resultList);
            */
        }

        return "itemResults";
    }
}
