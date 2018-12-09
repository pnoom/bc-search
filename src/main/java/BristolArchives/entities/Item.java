package BristolArchives.entities;

import javax.persistence.*;

@Entity
@Table(name="Item")  // This indicates 'Collection' objects are from mysql table 'Collection'
public class Item {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name="itemref")
    private String itemRef;

    @Column(name="name")
    private String name;

    @Column(name="location")
    private String location;

    @Column(name="description")
    private String description;

    @Column(name="datecreated")
    private String dateCreated;

    @Column(name="copyrighted")
    private String copyrighted;

    @Column(name="extent")
    private String extent;

    @Column(name="phystechdesc")
    private String physTechDesc;

    @ManyToOne
    @JoinColumn(name="subcollectionid")
    private Integer subCollectionId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItemRef() {
        return itemRef;
    }

    public void setItemRef(String itemRef) {
        this.itemRef = itemRef;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCopyrighted() {
        return copyrighted;
    }

    public void setCopyrighted(String copyrighted) {
        this.copyrighted = copyrighted;
    }

    public String getExtent() {
        return extent;
    }

    public void setExtent(String extent) {
        this.extent = extent;
    }

    public String getPhysTechDesc() {
        return physTechDesc;
    }

    public void setPhysTechDesc(String physTechDesc) {
        this.physTechDesc = physTechDesc;
    }

    public Integer getSubCollectionId() {
        return subCollectionId;
    }

    public void setSubCollectionId(Integer subCollectionId) {
        this.subCollectionId = subCollectionId;
    }
}