package BristolArchives.repositories;

import BristolArchives.entities.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionRepo extends JpaRepository<Collection,Integer>{
}
