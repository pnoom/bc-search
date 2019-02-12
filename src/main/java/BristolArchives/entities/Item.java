package BristolArchives.entities;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.Date;

@Entity(name="item")
@Table(name="item")  // This indicates 'Collection' objects are from mysql table 'Collection'
public class Item {

    public Item(){}

    public Item(String itemRef, String name, String location, String description, String dateCreated, String copyrighted, String extent, String physTechDesc, String multimediaIrn, SubCollection subCollection) {
        this.itemRef = itemRef;
        this.name = name;
        this.location = location;
        this.description = description;
        this.dateCreated = dateCreated;
        this.copyrighted = copyrighted;
        this.extent = extent;
        this.physTechDesc = physTechDesc;
        this.multimediaIrn = multimediaIrn;
        this.subCollection = subCollection;
    }

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

    @Column(name = "copyrighted")
    private String copyrighted;

    @Column(name = "extent")
    private String extent;

    @Column(name = "phys_tech_desc")
    private String physTechDesc;

    @Column(name = "multimedia_irn")
    private String multimediaIrn;

    @OneToOne
    @JoinColumn(name = "subcollection_id")
    private SubCollection subCollection;

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

    public String getMultimediaIrn(){return multimediaIrn;}

    public void setMultimediaIrn(String multimediaIrn) {
        this.multimediaIrn = multimediaIrn;
    }

    public String getPhysTechDesc() {
        return physTechDesc;
    }

    public void setPhysTechDesc(String physTechDesc) {
        this.physTechDesc = physTechDesc;
    }

    public SubCollection getSubCollection() {
        return subCollection;
    }

    public void setSubCollection(SubCollection subCollection) {
        this.subCollection = subCollection;
    }

    public String getShortDesc(){
        return description.length() > 280 ? description.substring(0,277) + "..." : description;
    }

    public String getURLOfImage(){
        return "http://museums.bristol.gov.uk/multimedia/entry.php?request=resource&irn=" + getMultimediaIrn() + "&format=jpeg.jpeg";
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

}