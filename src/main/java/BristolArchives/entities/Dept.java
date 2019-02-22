package BristolArchives.entities;

import javax.persistence.*;

@Entity(name="dept")
@Table(name="dept")
public class Dept {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Integer id;

    @Column(name="name")
    private String name;

    @Override
    public String toString() {
        return this.name;
    }

    public Dept() {};
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
}
