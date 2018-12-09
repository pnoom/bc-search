package BristolArchives.services;

import BristolArchives.entities.Item;
import BristolArchives.repositories.ItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Service
public class ItemService {
    @Autowired
    private ItemRepo itemRepo;

    public List<Item> getAllItems(){
        return itemRepo.findAll();
    }

    public Set<Item> getItem(String searchterm){
        String[] terms = searchterm.split(" ");
        Set<Item> results = new HashSet<Item>();
        for (String s: terms){
            List<Item> currResults = itemRepo.findByNameOrDescriptionOrLocationOrDateCreated(s);
            for(Item i: currResults){
                results.add(i);
            }
        }
        return results;
    }
}
