package BristolArchives.services;

import BristolArchives.entities.SubCollection;
import BristolArchives.repositories.SubCollectionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubCollectionService {
    @Autowired
    private SubCollectionRepo subCollectionRepo;

    public List<SubCollection> getAllSubCollections(){
        return subCollectionRepo.findAll();
    }
}

