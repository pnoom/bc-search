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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    // for printf only
    private String checkForNull(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        if (date == null) {
            return "";
        }
        else {
            return df.format(date);
        }
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

        //System.out.printf("collection: %s, single_date: %s, start: %s, end: %s, whole_phrase: %s, location: %s",
        //        adv_coll, checkForNull(adv_date), checkForNull(adv_date_start), checkForNull(adv_date_end), adv_name, adv_lctn);

        String search = "?";
        if(hasSth(adv_coll))
            search += "&coll=" + adv_coll;
        if(hasSth(adv_date_start))
            search += "&dateStart=" + adv_date_start;
        if(hasSth(adv_date_end))
            search += "&dateEnd=" + adv_date_end;
        if(hasSth(adv_date))
            search += "&date=" + adv_date;
        if(hasSth(adv_name))
            search += "&name=" + adv_name;
        if(hasSth(adv_lctn))
            search += "&lctn=" + adv_lctn;

        search = search.replaceAll("/","%2F");

        return "redirect:/advSearch" + search;
    }

    private Date parseDate(String str) {
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        try {
            return df.parse(str);
        }
        catch (ParseException e) {
            return new Date();
        }
    }

    @GetMapping("/advSearch")
    public String displayResult(
            @RequestParam(value = "coll", required = false) String adv_coll,
            @RequestParam(value = "date", required = false) String adv_date,
            @RequestParam(value = "dateStart", required = false) String adv_date_start,
            @RequestParam(value = "dateEnd", required = false) String adv_date_end,
            @RequestParam(value = "name", required = false) String adv_name,
            @RequestParam(value = "lctn", required = false) String adv_lctn,
            Model model
            ){

        if(!hasSth(adv_coll) && !hasSth(adv_date) && !hasSth(adv_name) && !hasSth(adv_lctn) && !hasSth(adv_date_start) && !hasSth(adv_date_end))
            return "redirect:/advanceSearch";

        if (hasSth(adv_date))
            model.addAttribute("itemList",
                    itemService.getAdvancedSearch(
                            parseDate(adv_date),
                            null,
                            null,
                            adv_coll,
                            adv_lctn,
                            adv_name));
        else {
            model.addAttribute("itemList",
                    itemService.getAdvancedSearch(
                            null,
                            parseDate(adv_date_start),
                            parseDate(adv_date_end),
                            adv_coll,
                            adv_lctn,
                            adv_name));
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
