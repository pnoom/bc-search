package BristolArchives;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages="BristolArchives.repositories", entityManagerFactoryRef="emf")
public class BristolArchivesApiApp {
    public static void main(String[] args){
        SpringApplication.run(BristolArchivesApiApp.class, args);
    }
}
