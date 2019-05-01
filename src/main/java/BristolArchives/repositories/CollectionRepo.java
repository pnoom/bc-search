package BristolArchives.repositories;

import BristolArchives.entities.Collection;
import BristolArchives.entities.Dept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionRepo extends JpaRepository<Collection, Integer> {

    List<Collection> findByNameContaining(String name);

    List<Collection> findByName(String name);

    List<Collection> findAll();

    @Query(value = "select * from collection order by name asc", nativeQuery = true)
    List<Collection> findAllAlphabeticalOrder();

    List<Collection> findByDept(Dept dept);

}
