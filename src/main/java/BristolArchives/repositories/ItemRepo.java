package BristolArchives.repositories;

import BristolArchives.entities.Item;
import BristolArchives.entities.SubCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepo extends JpaRepository<Item,Integer>{

    List<Item> findByName(String name);
    List<Item> findByNameContaining(String name);
    List<Item> findBySubCollection(SubCollection subColl);

    @Query("select i from item i where i.name like CONCAT('%',:search,'%')")
    List<Item> findName(@Param("search")String search);

    @Query("select i from item i where i.location like CONCAT('%',:search,'%')")
    List<Item> findLocation(@Param("search")String search);

    @Query("select i from item i where i.displayDate like CONCAT('%',:search,'%')")
    List<Item> findDate(@Param("search")String search);

    @Query("select i from item i where i.description like CONCAT('%',:search,'%')")
    List<Item> findDescription(@Param("search")String search);

    @Query("select i from item i where i.itemRef = CONCAT(:search,'')")
    List<Item> findWithRef(@Param("search")String search);

    @Query("select i from item i where i.start_date = :specificdate or :specificdate between i.start_date and i.end_date")
    List<Item> findBySpecificDate(@Param("specificdate")String specificdate);

    @Query("select i from item i where not (i.start_date > :start or i.end_date < :end)")
    List<Item> findByDateRange(@Param("start")String start, @Param("end")String end);
    
}
