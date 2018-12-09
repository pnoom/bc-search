package BristolArchives.repositories;

import BristolArchives.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepo extends JpaRepository<Item,Integer>{

}
