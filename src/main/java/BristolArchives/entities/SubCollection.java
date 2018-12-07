package BristolArchives.entities;

import javax.persistence.*;

@Entity
@Table(name="SubCollection")  // This indicates 'Collection' objects are from mysql table 'Collection'
public class SubCollection {

    @Id
    @Column(name="subCollectionId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="collectionId")
    private Integer collectionId;

    @Column(name="subCollectionDescription")
    private String description;

    @Column(name="subCollectionRef")
    private Integer subCollectionRef;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSubCollectionRef(){return subCollectionRef;}

    public void setSubCollectionRef(Integer subCollectionRef){this.subCollectionRef = subCollectionRef;}

    public SubCollection(Integer collectionId, Integer subCollectionRef, String description) {
        this.collectionId = collectionId;
        this.subCollectionRef = subCollectionRef;
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format(
                "Collection[id=%d, subcollection ref=%d, collection id='%s', description='%s']",
                id, subCollectionRef, collectionId, description);
    }

}
