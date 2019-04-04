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
    List<Item> findByCollectionLike(String string);
    List<Item> findByCollectionDisplayNameLike(String string);

    @Query(value = "select * from item join collection where item.collection_id = collection.id and (collection.name like concat('%', :collection, '%') or item.collection_display_name like concat('%', :collection, '%'))", nativeQuery = true)
    List<Item> findWithCollection(@Param("collection") String collection);

    @Query(value = "select * from item where name like concat('%', :phrase, '%') or description like concat('%', :phrase, '%')", nativeQuery = true)
    List<Item> findWholePhrase(@Param("phrase") String phrase);

    @Query(value = "select *, datediff(start_date, end_date) as closest from item where start_date = :specificdate or (:specificdate between start_date and end_date) order by closest desc", nativeQuery = true)
    List<Item> findWithSpecificDate(@Param("specificdate")Date specificdate);

    @Query(value = "select *, datediff(start_date, end_date) as closest from item where not (start_date > :start or end_date < :end) order by closest desc", nativeQuery = true)
    List<Item> findWithDateRange(@Param("start") Date start, @Param("end")Date end);

    @Query(value = "select * from item join collection on collection.id = item.collection_id join dept on collection.dept_id = dept.id where dept.name = dpt", nativeQuery = true)
    List<Item> findByDpt(@Param("dpt") String dpt);

}
