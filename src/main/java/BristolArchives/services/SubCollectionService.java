package BristolArchives.services;

import BristolArchives.entities.Collection;
import BristolArchives.entities.SubCollection;
import BristolArchives.repositories.SubCollectionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubCollectionService {

    @Autowired
    private SubCollectionRepo subCollectionRepo;

    @Autowired
    private CollectionService collectionService;

    public List<SubCollection> getAllSubCollections(){
        return subCollectionRepo.findAll();
    }

    public SubCollection getByName(String subCollName){
        return subCollectionRepo.findByName(subCollName);
    }

    public List<SubCollection> getByCollectionName(String collName){
        List<Collection> collList = collectionService.getByName(collName);
        List<SubCollection> result = new ArrayList<>();
        for(Collection coll : collList)
            result.addAll(subCollectionRepo.findByCollection(coll));
        return result;
    }

}

