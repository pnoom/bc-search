package BristolArchives.entities;

import javax.persistence.*;

@Entity
@Table(name="collection")  // This indicates entity 'Collection' is a object from MySQL table 'collection'
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Integer id;

    @Column(name="name")
    private String name;

    @Column(name="collectionref")
    // MySQL is not case sensitive. @Column(name="collectionRef") will be parse as column "collection_ref"
    private String collectionRef;

    @Column(name="description")
    private String description;

    public Collection(){}

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
}
