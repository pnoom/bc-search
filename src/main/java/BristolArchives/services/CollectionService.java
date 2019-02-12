package BristolArchives.services;

import BristolArchives.entities.Collection;
import BristolArchives.repositories.CollectionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionService {

    @Autowired
    private CollectionRepo collectionRepo;

    public List<Collection> getAllCollections(){
        return collectionRepo.findAll();
    }

    public List<Collection> getByNameContaining(String searchTerm){
        return collectionRepo.findByNameContaining(searchTerm);
    }

    public List<Collection> getByName(String searchTerm){
        return collectionRepo.findByName(searchTerm);
    }

}
