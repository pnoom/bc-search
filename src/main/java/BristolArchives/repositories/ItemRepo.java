package BristolArchives.repositories;

import BristolArchives.entities.Item;
import BristolArchives.entities.SubCollection;
import org.hibernate.annotations.NamedNativeQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.lang.annotation.Native;
import java.util.Date;
import java.util.List;

public interface ItemRepo extends JpaRepository<Item,Integer>{

    List<Item> findByName(String name);
    List<Item> findByNameContaining(String name);
    List<Item> findByItemRef(String ref);
    List<Item> findByDisplayDateLike(String date);
    List<Item> findByLocationLike(String location);
    List<Item> findByDescriptionContaining(String search);
    // List<Item> findBySubCollection(SubCollection subColl);

    //@Query("select i from item i where i.name like CONCAT('%',:search,'%')")
    //List<Item> findName(@Param("search")String search);

    //@Query("select i from item i where i.location like CONCAT('%',:search,'%')")
    //List<Item> findLocation(@Param("search")String search);

    //@Query("select i from item i where i.displayDate like CONCAT('%',:search,'%')")
    //List<Item> findDate(@Param("search")String search);

    //@Query("select i from item i where i.description like CONCAT('%',:search,'%')")
    //List<Item> findDescription(@Param("search")String search);

    //@Query(value = "select i from item i where i.itemRef = :search", nativeQuery = true)
    //List<Item> findWithRef(@Param("search")String search);

    //@Query("select i from item i where i.startDate = :specificdate or (:specificdate between i.startDate and i.endDate)")
    //List<Item> findWithSpecificDate(@Param("specificdate")Date specificdate);

    // These return INTEGERS tho, not Item entities...
    @Query(value = "select id from Item where start_date = :specificdate or (:specificdate between start_date and end_date)", nativeQuery = true)
    List<Item> findWithSpecificDate(@Param("specificdate")Date specificdate);

    @Query(value = "select id from Item where not (start_date > :start or end_date < :end)", nativeQuery = true)
    List<Item> findWithDateRange(@Param("start") Date start, @Param("end")Date end);

}
