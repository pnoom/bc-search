package BristolArchives.entities;

import javax.persistence.*;

@Entity(name="multimedia")
@Table(name="multimedia")
public class Multimedia {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "copyrighted")
    private String copyrighted;

    @Column(name = "irn")
    private Integer irn;

    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;

    public Multimedia() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCopyrighted() {
        return copyrighted;
    }

    public void setCopyrighted(String copyrighted) {
        this.copyrighted = copyrighted;
    }

    public Integer getIrn() {
        return irn;
    }

    public void setIrn(Integer irn) {
        this.irn = irn;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
