package BristolArchives.controllers;

import BristolArchives.entities.Item;
import BristolArchives.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AdvancedSearchController {
    @Autowired
    private ItemService itemService;

    @GetMapping("/advanceSearch")
    public String advanceSearch() {
        return "advanceSearch";
    }

    @PostMapping("/advSearch")
    public String sendResult(
            @RequestParam(value = "adv_search_coll", required = false) String adv_coll,
            @RequestParam(value = "adv_search_date", required = false) String adv_date,
            @RequestParam(value = "adv_search_name", required = false) String adv_name,
            @RequestParam(value = "adv_search_lctn", required = false) String adv_lctn
        )
    {
        String search = "redirect:/advSearch";

        if(!adv_coll.isEmpty() && adv_coll != null)
            search += "?coll=" + adv_coll;
        if(!adv_coll.isEmpty() && adv_date != null)
            search += "?date=" + adv_date ;
        if(!adv_coll.isEmpty() && adv_name != null)
            search += "?name=" + adv_name;
        if(!adv_coll.isEmpty() && adv_lctn != null)
            search += "?lctn=" + adv_lctn;

        search = search.replaceAll("/","%2F");

        return "redirect:/advSearch" + search;
    }

    @GetMapping("/advSearch")
    public String displayResult(
            @RequestParam(value = "coll", required = false) String adv_coll,
            @RequestParam(value = "date", required = false) String adv_date,
            @RequestParam(value = "name", required = false) String adv_name,
            @RequestParam(value = "lctn", required = false) String adv_lctn,
            Model model
            )
    {
        if(adv_coll == null && adv_date == null && adv_name == null && adv_lctn == null) {
            return "redirect:/advanceSearch";
        }
        else{
            List<Item> resultList = new ArrayList();
            if(adv_name != null)
                resultList.addAll(itemService.getItemByName(adv_name));
            if(adv_date != null)
                resultList.addAll(itemService.getItemByDate(adv_date));
//            if(!adv_coll != null)
//                resultList.addAll(itemService.getItemByCollectionID(adv_coll));
            if(adv_lctn != null)
                resultList.addAll(itemService.getItemByLocation(adv_lctn));
            model.addAttribute("itemList", resultList);

        }
        return "itemResults";
    }
}
