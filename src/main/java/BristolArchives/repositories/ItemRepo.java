package BristolArchives.repositories;

import BristolArchives.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepo extends JpaRepository<Item,Integer>{

    @Query("select i from item i where i.name like '%searchterm%' or i.description like '%searchterm%' or i.location like '%searchterm%' or i.date like '%searchterm%'")
    List<Item> findByNameOrDescriptionOrLocationOrDateCreated(String searchTerm);
}
