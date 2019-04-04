package BristolArchives.services;

import BristolArchives.entities.Dept;
import BristolArchives.repositories.DeptRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeptService {

    @Autowired
    private DeptRepo deptRepo;

    public List<Dept> getAllDepts(){
        return deptRepo.findAllAlphabeticalOrder();
    }

    public List<Dept> getByNameContaining(String searchTerm){
        return deptRepo.findByNameContaining(searchTerm);
    }

    public List<Dept> getByName(String searchTerm){
        return deptRepo.findByName(searchTerm);
    }

}
