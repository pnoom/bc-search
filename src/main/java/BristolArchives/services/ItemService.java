package BristolArchives.services;

import BristolArchives.entities.Item;
import BristolArchives.repositories.ItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ItemService {
    @Autowired
    private ItemRepo itemRepo;

    public List<Item> getAllItems(){
        return itemRepo.findAll();
    }

    public List<Item> getItem(String searchterm){
        String[] terms = searchterm.split(" ");
        List<Item> results = new ArrayList<>();
        for (String s: terms){
            List<Item> currResults = itemRepo.findExact(s);
            for(Item i: currResults){
                if (!results.contains(i))
                    results.add(i);
            }
            currResults = itemRepo.findName(s);
            for(Item i: currResults){
                if (!results.contains(i))
                    results.add(i);
            }
            currResults = itemRepo.findLocation(s);
            for(Item i: currResults){
                if (!results.contains(i))
                    results.add(i);
            }
            currResults = itemRepo.findDate(s);
            for(Item i: currResults){
                if (!results.contains(i))
                    results.add(i);
            }
            currResults = itemRepo.findDescription(s);
            for(Item i: currResults){
                if (!results.contains(i))
                    results.add(i);
            }

        }
        return results;
    }

    public Item getExactItem(String ref){
        return itemRepo.findWithRef(ref).get(0);
    }
}
