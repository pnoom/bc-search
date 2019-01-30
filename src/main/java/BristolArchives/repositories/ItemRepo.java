package BristolArchives.repositories;

import BristolArchives.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepo extends JpaRepository<Item,Integer>{

    @Query("select i from item i where i.name = CONCAT(:search,'')")
    List<Item> findByName(@Param("search")String search);

    @Query("select i from item i where i.name like CONCAT('%',:search,'%')")
    List<Item> findName(@Param("search")String search);

    @Query("select i from item i where i.location like CONCAT('%',:search,'%')")
    List<Item> findLocation(@Param("search")String search);

    @Query("select i from item i where i.dateCreated like CONCAT('%',:search,'%')")
    List<Item> findDate(@Param("search")String search);

    @Query("select i from item i where i.description like CONCAT('%',:search,'%')")
    List<Item> findDescription(@Param("search")String search);

    @Query("select i from item i where i.itemRef = CONCAT(:search,'')")
    List<Item> findWithRef(@Param("search")String search);

    @Query("select i from item i where i.subCollection = CONCAT(:search,'')")
    List<Item> findSubCollection(@Param("search")String search);

}
