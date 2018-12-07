package BristolArchives.entities;

import javax.persistence.*;

@Entity
@Table(name="Collection")  // This indicates 'Collection' objects are from mysql table 'Collection'
public class Collection {
    @Id
    @Column(name="collectionId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name="collectionRef")
    private String collectionRef;
    @Column(name="collectionName")
    private String name;
    @Column(name="collectionDescription")
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCollectionRef() {
        return collectionRef;
    }

    public void setCollectionRef(String collectionRef){this.collectionRef = collectionRef;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format(
                "Collection[id=%d, object number='%s', name='%s', description='%s']",
                id, collectionRef, name, description);
    }

}
