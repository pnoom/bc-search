package BristolArchives.entities;

import javax.persistence.*;

@Entity(name="collection")
@Table(name="collection")  // This indicates entity 'Collection' is a object from MySQL table 'collection'
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Integer id;

    @Column(name="name")
    private String name;

    @OneToOne
    @JoinColumn(name = "dept_id")
    private Dept dept;

    public Collection() {};

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public Dept getDept() {
        return dept;
    }

    public void setDept(Dept dept) {
        this.dept = dept;
    }
}
