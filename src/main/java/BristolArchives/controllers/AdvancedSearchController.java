package BristolArchives.controllers;

import BristolArchives.entities.Item;
import BristolArchives.services.CollectionService;
import BristolArchives.services.DeptService;
import BristolArchives.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class AdvancedSearchController {
    @Autowired
    private ItemService itemService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private DeptService deptService;

    private boolean hasSth(String s) {
        return (s != null) && (!s.isEmpty());
    }

    @GetMapping("/advancedSearch")
    public String advanceSearch(Model model) {
        model.addAttribute("collectionList",collectionService.getAllCollections());
        model.addAttribute("deptList",deptService.getAllDepts());
        return "advancedSearch";
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
            @RequestParam(value = "collection_search", required = false) String coll,
            @RequestParam(value = "date_search", required = false) String date,
            @RequestParam(value = "date_start", required = false) String date_start,
            @RequestParam(value = "date_end", required = false) String date_end,
            @RequestParam(value = "precision_search", required = false) String name,
            @RequestParam(value = "location_search", required = false) String lctn,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size
        ){
        String search = "?";

        if(hasSth(coll))
            search += "&coll=" + coll;
        if(hasSth(date_start))
            search += "&dateStart=" + date_start;
        if(hasSth(date_end))
            search += "&dateEnd=" + date_end;
        if(hasSth(date))
            search += "&date=" + date;
        if(hasSth(name))
            search += "&name=" + name;
        if(hasSth(lctn))
            search += "&lctn=" + lctn;

        search = search.replaceAll("/","%2F");

        return "redirect:/advSearch" + search + "&page=" + page.orElse(1) + "&size=" + size.orElse(5);
    }

    private Date parseDate(String str) {
        if(str == "")
            return null;

        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        try {
            return df.parse(str);
        }
        catch (ParseException e) {
            return new Date();
        }
    }

    private void displayPageNumber(Model model, Page<Item> itemPage, int currentPage) {
        int totalPages = itemPage.getTotalPages();
        List<Integer> pageNumbers;
        currentPage = Math.min(totalPages, currentPage);

        if (totalPages > 0) {
            int startPage, endPage;

            if(totalPages <= 5) {
                startPage = 1;
                endPage = totalPages;
            } else {
                startPage = Math.max(currentPage - 2, 1);
                endPage = Math.max(5, Math.min(currentPage + 2, totalPages));
            }
            pageNumbers = IntStream.rangeClosed(startPage, endPage)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
    }

    @GetMapping("/advSearch")
    public String displayResult(
            @RequestParam(value = "coll", required = false) String coll,
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "dateStart", required = false) String date_start,
            @RequestParam(value = "dateEnd", required = false) String date_end,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "lctn", required = false) String lctn,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            Model model
            ){

        if(!hasSth(coll) && !hasSth(date) && !hasSth(name) && !hasSth(lctn) && !hasSth(date_start) && !hasSth(date_end))
            return "redirect:/advancedSearch";

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        date = hasSth(date) ? date : "";
        date_start = hasSth(date_start) ? date_start : "";
        date_end = hasSth(date_end) ? date_end : "";

//        System.out.println(date);
//        System.out.println(date_start);
//        System.out.println(date_end);
//        System.out.println();

        Page<Item> itemPage = itemService.findPaginatedAdvSearch(
                parseDate(date),
                parseDate(date_start),
                parseDate(date_end),
                coll,
                lctn,
                name,
                PageRequest.of(Math.max(currentPage - 1, 0), Math.max(pageSize, 1))
        );

        model.addAttribute("itemPage", itemPage);
        model.addAttribute("specificDate", date);
        model.addAttribute("startDate", date_start);
        model.addAttribute("endDate", date_end);
        model.addAttribute("collection", coll);
        model.addAttribute("location", lctn);
        model.addAttribute("precision", name);

        displayPageNumber(model, itemPage, currentPage);


        if (hasSth(date)) {
            System.out.println("advanced date");
            /*model.addAttribute("itemList",
                    itemService.getAdvancedSearch(
                            parseDate(adv_date),
                            null,
                            null,
                            adv_coll,
                            adv_lctn,
                            adv_name));*/

            itemPage = itemService.findPaginatedAdvSearch(parseDate(date),
                    null,
                    null,
                    coll,
                    lctn,
                    name, PageRequest.of(currentPage - 1, pageSize));

            model.addAttribute("itemPage", itemPage);
            model.addAttribute("specificDate", date);
            model.addAttribute("startDate", "");
            model.addAttribute("endDate", "");
            model.addAttribute("collection", coll);
            model.addAttribute("location", lctn);
            model.addAttribute("precision", name);

            displayPageNumber(model, itemPage, currentPage);
        }
        if(hasSth(date_start) && hasSth(date_end)) {
           /* model.addAttribute("itemList",
                    itemService.getAdvancedSearch(
                            null,
                            parseDate(adv_date_start),
                            parseDate(adv_date_end),
                            adv_coll,
                            adv_lctn,
                            adv_name));*/

            itemPage = itemService.findPaginatedAdvSearch(null, parseDate(date_start),
                    parseDate(date_end),
                    coll,
                    lctn,
                    name, PageRequest.of(currentPage - 1, pageSize));

            model.addAttribute("itemPage", itemPage);
            model.addAttribute("specificDate", "");
            model.addAttribute("startDate", date_start);
            model.addAttribute("endDate", date_end);
            model.addAttribute("collection", coll);
            model.addAttribute("location", lctn);
            model.addAttribute("precision", name);

            displayPageNumber(model, itemPage, currentPage);
        }
        if(!hasSth(date) && (!hasSth(date_start) || !hasSth(date_end))){
            /*model.addAttribute("itemList",
                    itemService.getAdvancedSearch(
                            null,
                            null,
                            null,
                            adv_coll,
                            adv_lctn,
                            adv_name));*/

            itemPage = itemService.findPaginatedAdvSearch(null,
                    null,
                    null,
                    coll,
                    lctn,
                    name, PageRequest.of(currentPage - 1, pageSize));

            model.addAttribute("itemPage", itemPage);
            model.addAttribute("specificDate", "");
            model.addAttribute("startDate", "");
            model.addAttribute("endDate", "");
            model.addAttribute("collection", coll);
            model.addAttribute("location", lctn);
            model.addAttribute("precision", name);

            displayPageNumber(model, itemPage, currentPage);
        }

        return "advSearchResults";
    }
}
