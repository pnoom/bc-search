package BristolArchives.controllers;

import BristolArchives.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Optional;
import BristolArchives.entities.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping("/search")
    public String sendResult(@RequestParam(value = "main_search", required = false) String search,  @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size){
        if(search == null || search == "") {
            System.out.println("search query null post");
        }
        search = search.replaceAll("/","%2F");
        return "redirect:/search?q=" + search + "&page=" + page.orElse(1) + "&size=" + size.orElse(5);
    }

    @GetMapping("/search")
    public String displayResult(@RequestParam(required = false) String q , @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, Model model){
        if(q == null || q == "") {
            System.out.println("search query null get");
            //model.addAttribute("collectionsResults", itemService.getItem(search));
            return "redirect:/";
        } else {
            int currentPage = page.orElse(1);
            int pageSize = size.orElse(5);

            Page<Item> itemPage = itemService.findPaginated(q, PageRequest.of(currentPage - 1, pageSize));

            model.addAttribute("itemPage", itemPage);
            model.addAttribute("searchTerm", q);

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
            return "paginatedItemResults";
        }
    }

//    public String displayResult(@RequestParam(value = "main_search", required = false) String search, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, Model model) {

/*
    @GetMapping("/search")
    public String displayResult(@RequestParam(required = false) String q , Model model){
        if(q == null || q == "") {
            //model.addAttribute("collectionsResults", itemService.getItem(search));
            return "redirect:/";
        }
        else{
            model.addAttribute("itemList", itemService.getItem(q));
        }
        return "itemResults";
    }
*/
    @GetMapping("/items/{itemRef}")
    public String displaySingleItem(@PathVariable String itemRef, Model model) {
        itemRef = itemRef.replace('-','/');

        if(itemRef == null) {
            //model.addAttribute("collectionsResults", itemService.getItem(search));
            return "redirect:/";
        }
        else{
            Item item = itemService.getExactItem(itemRef);
            model.addAttribute("item", item);
            model.addAttribute("firstMediaIrn", itemService.getFirstMultimediaIrn(item));
        }
        return "itemPage";
    }


}


