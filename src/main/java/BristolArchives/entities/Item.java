package BristolArchives.entities;

import javax.persistence.*;

@Entity
@Table(name="Item")  // This indicates 'Collection' objects are from mysql table 'Collection'
public class Item {

    @Id
    @Column(name="itemId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name="itemRef")
    private String itemRef;

    @Column(name="itemName")
    private String name;

    @Column(name="itemDescription")
    private String itemDescription;

    @Column(name="dateCreated")
    private String dateCreated;

    @Column(name="copyrighted")
    private String copyrighted;

    @Column(name="extent")
    private String extent;

    @Column(name="physTechDesc")
    private String physTechDesc;

    @ManyToOne
    @JoinColumn(name="subCollectionId")
    private Integer subCollectionId;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setItemRef(String itemRef) {
        this.itemRef = itemRef;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setCopyrighted(String copyrighted) {
        this.copyrighted = copyrighted;
    }

    public void setExtent(String extent) {
        this.extent = extent;
    }

    public void setPhysTechDesc(String physTechDesc) {
        this.physTechDesc = physTechDesc;
    }

    public void setSubCollectionId(Integer subCollectionId) {
        this.subCollectionId = subCollectionId;
    }

    public Integer getId() {
        return id;
    }

    public String getItemRef() {
        return itemRef;
    }

    public String getName() {
        return name;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String getCopyrighted() {
        return copyrighted;
    }

    public String getExtent() {
        return extent;
    }

    public String getPhysTechDesc() {
        return physTechDesc;
    }

    public Integer getSubCollectionId() {
        return subCollectionId;
    }

    public Item(String itemRef, String name, String itemDescription, String dateCreated, String copyrighted, String extent, String physTechDesc, Integer subCollectionId) {
        this.itemRef = itemRef;
        this.name = name;
        this.itemDescription = itemDescription;
        this.dateCreated = dateCreated;
        this.copyrighted = copyrighted;
        this.extent = extent;
        this.physTechDesc = physTechDesc;
        this.subCollectionId = subCollectionId;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "Item{" +
                "id=" + id +
                ", itemRef='" + itemRef + '\'' +
                ", name='" + name + '\'' +
                ", itemDescription='" + itemDescription + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", copyrighted='" + copyrighted + '\'' +
                ", extent='" + extent + '\'' +
                ", physTechDesc='" + physTechDesc + '\'' +
                ", subCollectionId=" + subCollectionId +
                '}';
    }

}
