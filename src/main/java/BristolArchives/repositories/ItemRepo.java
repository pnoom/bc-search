package BristolArchives.repositories;

import BristolArchives.entities.Collection;
import BristolArchives.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ItemRepo extends JpaRepository<Item,Integer>{

    List<Item> findByName(String name);
    List<Item> findByNameLike(String name);
    List<Item> findByNameContaining(String name);
    List<Item> findByItemRef(String ref);
    List<Item> findByItemRefIn(List<String> refs);
    List<Item> findByDisplayDateLike(String date);
    List<Item> findByLocationLike(String location);
    List<Item> findByDescriptionContaining(String search);
    List<Item> findByCollection(Collection coll);

    @Query(value = "select *, datediff(start_date, end_date) as closest from item where start_date = :specificdate or (:specificdate between start_date and end_date) order by closest desc", nativeQuery = true)
    List<Item> findWithSpecificDate(@Param("specificdate")Date specificdate);

    @Query(value = "select *, datediff(start_date, end_date) as closest from item where not (start_date > :start or end_date < :end) order by closest desc", nativeQuery = true)
    List<Item> findWithDateRange(@Param("start") Date start, @Param("end")Date end);

    // Simple search stuff
    //@Query(value = "", nativeQuery = true)
    //List<Item> performSimpleSearch(@Param("phrase") String phrase);

    // Advanced search stuff

}
