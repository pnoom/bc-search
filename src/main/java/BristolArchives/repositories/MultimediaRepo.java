package BristolArchives.repositories;

import BristolArchives.entities.Multimedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MultimediaRepo extends JpaRepository<Multimedia,Integer> {

    List<Multimedia> findAll();
}
