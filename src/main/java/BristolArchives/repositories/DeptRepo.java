package BristolArchives.repositories;

import BristolArchives.entities.Dept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeptRepo extends JpaRepository<Dept, Integer> {
    List<Dept> findByNameContaining(String name);

    List<Dept> findByName(String name);

    List<Dept> findAll();

    @Query(value = "select * from dept order by name asc", nativeQuery = true)
    List<Dept> findAllAlphabeticalOrder();
}
