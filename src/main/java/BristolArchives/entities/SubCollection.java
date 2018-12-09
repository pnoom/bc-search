package BristolArchives.entities;

import javax.persistence.*;

@Entity(name="SubCollection")
@Table(name="subcollection")
public class SubCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name="collectionid")
    private Integer collectionId;

    @Column(name="subcollectionref")
    private String subCollectionRef;

    @Column(name="name")
    private String name;

    @Column(name="description")
    private String description;

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

    public String getSubCollectionRef() {
        return subCollectionRef;
    }

    public void setSubCollectionRef(String subCollectionRef) {
        this.subCollectionRef = subCollectionRef;
    }

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

    @Override
    public String toString() {
        return this.subCollectionRef;
    }
}
