package BristolArchives.entities;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.Date;

@Entity(name="item")
@Table(name="item")  // This indicates 'Collection' objects are from mysql table 'Collection'
public class Item {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "item_ref")
    private String itemRef;

    @Column(name = "name")
    private String name;

    @Column(name = "location")
    private String location;

    @Column(name = "description")
    private String description;

    @Column(name="start_date")
    private Date startDate;

    @Column(name="end_date")
    private Date endDate;

    @Column(name = "display_date")
    private String displayDate;

    @Column(name = "extent")
    private String extent;

    @Column(name = "phys_tech_desc")
    private String physTechDesc;

    @OneToOne
    @JoinColumn(name = "collection_id")
    private Collection collection;

    @Column(name = "collection_display_name")
    private String collectionDisplayName;

    public Item(){};

    public Item(String itemRef, String name, String location, String description, Date startDate, Date endDate, String displayDate, String extent, String physTechDesc, Collection collection, String collectionDisplayName) {
        this.itemRef = itemRef;
        this.name = name;
        this.location = location;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.displayDate = displayDate;
        this.extent = extent;
        this.physTechDesc = physTechDesc;
        this.collection = collection;
        this.collectionDisplayName = collectionDisplayName;
    }

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

    public String getShortDesc(){
        return description.length() > 280 ? description.substring(0,277) + "..." : description;
    }

    // TODO: move elsewhere
    public String getURLOfImage(){
        return ""; // http://museums.bristol.gov.uk/multimedia/entry.php?request=resource&irn=" + getMultimediaIrn() + "&format=jpeg.jpeg";
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDisplayDate() {
        return displayDate;
    }

    public void setDisplayDate(String displayDate) {
        this.displayDate = displayDate;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public String getCollectionDisplayName() {
        return collectionDisplayName;
    }

    public void setCollectionDisplayName(String collectionDisplayName) {
        this.collectionDisplayName = collectionDisplayName;
    }
}