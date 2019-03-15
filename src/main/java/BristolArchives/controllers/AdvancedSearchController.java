package BristolArchives.controllers;

import BristolArchives.entities.Item;
import BristolArchives.services.CollectionService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @GetMapping("/advancedSearch")
    public String advanceSearch(Model model) {
        //model.addAttribute("collectionList",collectionService.getAllCollections());
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

        //System.out.printf("collection: %s, single_date: %s, start: %s, end: %s, whole_phrase: %s, location: %s",
        //        adv_coll, checkForNull(adv_date), checkForNull(adv_date_start), checkForNull(adv_date_end), adv_name, adv_lctn);

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

    private String searchString(@RequestParam(value = "collection_search", required = false) String adv_coll,
                         @RequestParam(value = "date_search", required = false) String adv_date,
                         @RequestParam(value = "date_start", required = false) String adv_date_start,
                         @RequestParam(value = "date_end", required = false) String adv_date_end,
                         @RequestParam(value = "precision_search", required = false) String adv_name,
                         @RequestParam(value = "location_search", required = false) String adv_lctn){
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
        return search;
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
            int currentPage = page.orElse(1);
            int pageSize = size.orElse(5);

            Page<Item> itemPage = itemService.findPaginatedAdvSearch(parseDate(date),
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



            int totalPages = itemPage.getTotalPages();
            List<Integer> pageNumbers = new ArrayList();
            if (totalPages > 0) {
                if (currentPage >= 3 && currentPage <= totalPages - 2) {
                    pageNumbers = IntStream.rangeClosed(currentPage - 2, Math.min(currentPage + 2, totalPages))
                            .boxed()
                            .collect(Collectors.toList());
                }
                else if(currentPage < 3){
                    pageNumbers = IntStream.rangeClosed(1, Math.min(5,totalPages))
                            .boxed()
                            .collect(Collectors.toList());
                }
                else if(currentPage > totalPages - 2){
                    pageNumbers = IntStream.rangeClosed(totalPages - 5, totalPages)
                            .boxed()
                            .collect(Collectors.toList());
                }
                model.addAttribute("pageNumbers", pageNumbers);
            }
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
            int currentPage = page.orElse(1);
            int pageSize = size.orElse(5);

            Page<Item> itemPage = itemService.findPaginatedAdvSearch(null, parseDate(date_start),
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

            int totalPages = itemPage.getTotalPages();
            List<Integer> pageNumbers = new ArrayList();
            if (totalPages > 0) {
                if (currentPage >= 3 && currentPage <= totalPages - 2) {
                    pageNumbers = IntStream.rangeClosed(currentPage - 2, Math.min(currentPage + 2, totalPages))
                            .boxed()
                            .collect(Collectors.toList());
                }
                else if(currentPage < 3){
                    pageNumbers = IntStream.rangeClosed(1, Math.min(5,totalPages))
                            .boxed()
                            .collect(Collectors.toList());
                }
                else if(currentPage > totalPages - 2){
                    pageNumbers = IntStream.rangeClosed(totalPages - 5, totalPages)
                            .boxed()
                            .collect(Collectors.toList());
                }
                model.addAttribute("pageNumbers", pageNumbers);
            }
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
        if(!hasSth(date) && (!hasSth(date_start) || !hasSth(date_end))){
            /*model.addAttribute("itemList",
                    itemService.getAdvancedSearch(
                            null,
                            null,
                            null,
                            adv_coll,
                            adv_lctn,
                            adv_name));*/
            int currentPage = page.orElse(1);
            int pageSize = size.orElse(5);

            Page<Item> itemPage = itemService.findPaginatedAdvSearch(null,
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

            int totalPages = itemPage.getTotalPages();
            List<Integer> pageNumbers = new ArrayList();
            if (totalPages > 0) {
                if (currentPage >= 3 && currentPage <= totalPages - 2) {
                    pageNumbers = IntStream.rangeClosed(currentPage - 2, Math.min(currentPage + 2, totalPages))
                            .boxed()
                            .collect(Collectors.toList());
                }
                else if(currentPage < 3){
                    pageNumbers = IntStream.rangeClosed(1, Math.min(5,totalPages))
                            .boxed()
                            .collect(Collectors.toList());
                }
                else if(currentPage > totalPages - 2){
                    pageNumbers = IntStream.rangeClosed(totalPages - 5, totalPages)
                            .boxed()
                            .collect(Collectors.toList());
                }
                model.addAttribute("pageNumbers", pageNumbers);
            }

        }


        return "advSearchResults";
    }
}
