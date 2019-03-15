package BristolArchives.services;

import BristolArchives.entities.Item;
import BristolArchives.entities.Collection;
import BristolArchives.repositories.ItemRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ItemService {
    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private CollectionService collectionService;

    public List<Item> getAllItems(){
        return itemRepo.findAll();
    }

    public List<Item> getItemByNameContaining(String name) {
        return itemRepo.findByNameContaining(name);
    }

    /*
    public List<Item> getItemByCollectionName(String collName) {
        List<SubCollection> subCollList = subCollectionService.getByCollectionName(collName);
        List<Item> result = new ArrayList<>();
        for (SubCollection subColl : subCollList)
            result.addAll(itemRepo.findBySubCollection(subColl));
        return result;
    }
    */

//    public List<Item> getItemBySubCollectionName(String subCollName) {
//        return itemRepo.findBySubCollection(subCollectionService.getByName(subCollName).getId());
//    }


    public List<Item> getItemByCollectionName(String collName) {
        List<Item> result = new ArrayList<>();
        List<Collection> collList = collectionService.getByName(collName);
        for (Collection coll : collList)
            result.addAll(itemRepo.findByCollection(coll));
        return result;
    }

    public List<Item> getItemByName(String name) {
        return itemRepo.findByName(name);
    }

    public List<Item> getItemByLocation(String location) {
        return itemRepo.findByLocationLike(location);
    }

    public List<Item> getItemByDate(String date) {
        return itemRepo.findByDisplayDateLike(date);
    }

    private void getIntersection(List<Item> result, List<Item> newItems, boolean someConstraintsExist) {
        if(result.isEmpty() && !someConstraintsExist)
            result.addAll(newItems);
        else
            result.retainAll(newItems);
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
            currResults = itemRepo.findByItemRef(s);
            for(Item i: currResults){
                if (!results.contains(i))
                    results.add(i);
            }
            currResults = itemRepo.findByName(s);
            for(Item i: currResults){
                if (!results.contains(i))
                    results.add(i);
            }
            currResults = itemRepo.findByLocationLike(s);
            for(Item i: currResults){
                if (!results.contains(i))
                    results.add(i);
            }
            currResults = itemRepo.findByDisplayDateLike(s);
            for(Item i: currResults){
                if (!results.contains(i))
                    results.add(i);
            }
            currResults = itemRepo.findByDescriptionContaining(s);
            for(Item i: currResults){
                if (!results.contains(i))
                    results.add(i);
            }
        }

        return results;
    }

    public Item getExactItem(String ref){
        if(itemRepo.findByItemRef(ref).size() <= 0)
            return null;
        return itemRepo.findByItemRef(ref).get(0);
    }

    private boolean hasSth(String s) {
        return (s != null) && (!s.isEmpty());
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

    public List<Item> getAdvancedSearch(Date specific_date, Date start_date, Date end_date, String collection, String location, String precision) {
        List<Item> results = new ArrayList<>();
        boolean someConstraintsExist = false;
        //System.out.printf("collection: %s, single_date: %s, start: %s, end: %s, whole_phrase: %s, location: %s",
        //        collection, checkForNull(specific_date), checkForNull(start_date), checkForNull(end_date), precision, location);

        if (specific_date != null) {

            //List<Item> currResults = itemRepo.findBySpecificDate(specific_date);
            //for(Item i: currResults){
            //    if (!results.contains(i))
            //        results.add(i);
            // }
            getIntersection(results,itemRepo.findWithSpecificDate(specific_date),false);
            someConstraintsExist = true;
        }
        if (start_date != null && end_date != null) {
            //System.out.println("The if worked");
            getIntersection(results,itemRepo.findWithDateRange(start_date,end_date),someConstraintsExist);
            someConstraintsExist = true;
            /*List<Item> currResults = itemRepo.findByDateRange(start_date, end_date);
            for(Item i: currResults){
                if (!results.contains(i))
                    results.add(i);
            }*/
        }

        if (hasSth(collection)) {
            getIntersection(results,getItemByCollectionName(collection),someConstraintsExist);
            someConstraintsExist = true;
            List<Item> currResults = getItemByCollectionName(collection);
            for(Item i: currResults){
                if (!results.contains(i))
                    results.add(i);
            }
        }

        if (hasSth(location)) {
            getIntersection(results,itemRepo.findByLocationLike(location),someConstraintsExist);
            someConstraintsExist = true;
            /*List<Item> currResults = itemRepo.findLocation(location);
            for(Item i: currResults){
                if (!results.contains(i))
                    results.add(i);
            }*/
        }
        if (hasSth(precision)) {
            getIntersection(results,getItemByName(precision),someConstraintsExist);
            someConstraintsExist = true;
            /*List<Item> currResults = getItemByName(precision);
            for(Item i: currResults){
                if (!results.contains(i))
                    results.add(i);
            }*/
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
