package BristolArchives.entities;

import javax.persistence.*;

@Entity
@Table(name="Collection")  // This indicates 'Collection' objects are from mysql table 'Collection'
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    private String collectionRef;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCollectionRef() {
        return collectionRef;
    }

    public void setCollectionRef(String collectionRef) {
        this.collectionRef = collectionRef;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

}
