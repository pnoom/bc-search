package BristolArchives.entities;

import javax.persistence.*;

@Entity
@Table(name="subcollection")
public class SubCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Integer id;

    @Column(name="subcollectionref")
    private String subCollectionRef;

    @Column(name="description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "collectionid")
    private Collection collectionId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubCollectionRef() {
        return subCollectionRef;
    }

    public void setSubCollectionRef(String subCollectionRef) {
        this.subCollectionRef = subCollectionRef;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Collection collectionId) {
        this.collectionId = collectionId;
    }

    @Override
    public String toString() {
        return this.subCollectionRef;
    }
}
