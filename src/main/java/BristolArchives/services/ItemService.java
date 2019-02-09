package BristolArchives.services;

import BristolArchives.entities.Item;
import BristolArchives.entities.SubCollection;
import BristolArchives.repositories.ItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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

    public List<Item> getAdvancedSearch(String specific_date, Date start_date, Date end_date, String collection, String location, String precision) {
        List<Item> results = new ArrayList<>();
        if (specific_date != null) {
            results.addAll(itemRepo.findBySpecificDate(specific_date));
        }
        if (start_date != null && end_date != null) {
            List<Item> currResults = itemRepo.findByDateRange(start_date, end_date);
            for(Item i: currResults){
                if (!results.contains(i))
                    results.add(i);
            }
        }
        /*
        if (collection != null) {
            List<Item> currResults = itemRepo.findByCollection(collection);
            for(Item i: currResults){
                if (!results.contains(i))
                    results.add(i);
            }
        }
        */
        if (location != null) {
            List<Item> currResults = itemRepo.findLocation(location);
            for(Item i: currResults){
                if (!results.contains(i))
                    results.add(i);
            }
        }
        // this.getItem(precision, results); // TODO: getItem should take results param and mutate it
        return results;
    }

    public Page<Item> findPaginated(String searchquery, Pageable pageable) {
        final List<Item> items = getItem(searchquery);
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Item> list;

        if (items.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, items.size());
            list = items.subList(startItem, toIndex);
        }

        Page<Item> itemPage
                = new PageImpl<Item>(list, PageRequest.of(currentPage, pageSize), items.size());

        return itemPage;
    }

}
