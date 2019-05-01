package BristolArchives.entities;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity(name="item")
@Table(name="item")  // This indicates 'Collection' objects are from mysql table 'Collection'
public class Item {

    @Id
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

    @Column(name = "media_irns")
    private String mediaIrns;

    @Column(name = "media_count")
    private Integer mediaCount;

    @Column(name = "thumbnail_irn")
    private String thumbnailIrn;

    @Column(name = "copyrighted")
    private String copyrighted;

    @OneToOne
    @JoinColumn(name = "collection_id")
    private Collection collection;

    @Column(name = "collection_display_name")
    private String collectionDisplayName;

    public Item(){};

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

    public void setThumbnailIrn() {
        this.thumbnailIrn = getFirstMultimediaIrn();
    }

    public List<String> getMultimediaIrns() {
        if (this.getMediaIrns() != null)
            return new ArrayList<>(Arrays.asList(this.getMediaIrns().split(",")));
        else
            return null;
    }

    public String getFirstMultimediaIrn() {
        List<String> all = getMultimediaIrns();
        //System.out.println(all);
        if (all != null) {
            if (all.isEmpty()) {
                return "";
            } else {
                return all.get(0);
            }
        } else
            return "";
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

    public Integer getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(Integer mediaCount) {
        this.mediaCount = mediaCount;
    }

    public String getCopyrighted() {
        return copyrighted;
    }

    public void setCopyrighted(String copyrighted) {
        this.copyrighted = copyrighted;
    }

    public String getMediaIrns() {
        return mediaIrns;
    }

    public void setMediaIrns(String mediaIrns) {
        this.mediaIrns = mediaIrns;
    }

    public String getThumbnailIrn() {
        return thumbnailIrn;
    }
}
