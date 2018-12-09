package BristolArchives.entities;

import javax.persistence.*;

@Entity(name="SubCollection")
@Table(name="subcollection")
public class SubCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "collectionid")
    private Collection collection;

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

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
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
