package BristolArchives.services;

import BristolArchives.entities.Item;
import BristolArchives.entities.SubCollection;
import BristolArchives.repositories.ItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ItemService {
    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private SubCollectionService subCollectionService;

    public List<Item> getAllItems(){
        return itemRepo.findAll();
    }

    public List<Item> getItemByNameContaining(String name) {
        return itemRepo.findByNameContaining(name);
    }

    public List<Item> getItemByCollectionName(String collName) {
        List<SubCollection> subCollList = subCollectionService.getByCollectionName(collName);
        List<Item> result = new ArrayList<>();
        for (SubCollection subColl : subCollList)
            result.addAll(itemRepo.findBySubCollection(subColl));
        return result;
    }

//    public List<Item> getItemBySubCollectionName(String subCollName) {
//        return itemRepo.findBySubCollection(subCollectionService.getByName(subCollName).getId());
//    }

    public List<Item> getItemByName(String name) {
        return itemRepo.findByName(name);
    }

    public List<Item> getItemByLocation(String location) {
        return itemRepo.findLocation(location);
    }

    public List<Item> getItemByDate(String date) {
        return itemRepo.findDate(date);
    }

    public List<Item> getItem(String searchterm){
        String[] terms = searchterm.split(" ");
        List<Item> results = new ArrayList<>();
        for (String s: terms){
            List<Item> currResults = itemRepo.findByName(s);
            for(Item i: currResults){
                if (!results.contains(i))
                    results.add(i);
            }
            currResults = itemRepo.findWithRef(s);
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
        if(itemRepo.findWithRef(ref).size() <= 0)
            return null;
        return itemRepo.findWithRef(ref).get(0);
    }

}
