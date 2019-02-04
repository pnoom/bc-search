package BristolArchives.repositories;

import BristolArchives.entities.Collection;
import BristolArchives.entities.SubCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCollectionRepo extends JpaRepository<SubCollection,Integer> {

    List<SubCollection> findByNameContaining(String name);

    SubCollection findByName(String name);

    List<SubCollection> findAll();

    List<SubCollection> findByCollection(Collection coll);

}
