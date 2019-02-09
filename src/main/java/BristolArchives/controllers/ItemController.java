package BristolArchives.controllers;

import BristolArchives.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public String sendResult(@RequestParam(value = "main_search", required = false) String search){
        search = search.replaceAll("/","%2F");
        return "redirect:/search?q=" + search;
    }

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

    @GetMapping("/items/{itemRef}")
    public String displaySingleItem(@PathVariable String itemRef, Model model) {
        itemRef = itemRef.replace('-','/');

        if(itemRef == null) {
            //model.addAttribute("collectionsResults", itemService.getItem(search));
            return "redirect:/";
        }
        else{
            model.addAttribute("item", itemService.getExactItem(itemRef));
        }
        return "itemPage";
    }

    @GetMapping(value = "/listItems")
    public String listItems(Model model, @RequestParam(value = "main_search", required = false) String search, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        Page<Item> itemPage = itemService.findPaginated(search,PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("itemPage", itemPage);

        int totalPages = itemPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "paginatedItemResults";
    }

}


