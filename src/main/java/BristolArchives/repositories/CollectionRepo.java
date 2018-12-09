package BristolArchives.repositories;

import BristolArchives.entities.Collection;
import BristolArchives.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectionRepo extends JpaRepository<Collection, Integer>{
    List<Collection> findByNameContaining(String name);
    List<Collection> findAll();
}
