package BristolArchives.repositories;

import BristolArchives.entities.Dept;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeptRepo extends JpaRepository<Dept, Integer> {
    List<Dept> findByNameContaining(String name);

    List<Dept> findByName(String name);

    List<Dept> findAll();
}
