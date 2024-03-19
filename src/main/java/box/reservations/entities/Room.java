package box.reservations.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany
    private List<User> usersAssigned;


    @ManyToMany
    private List<SubCategory> subcategoriesAssigned;

    public Room(String name, SubCategory subCategory) {
        this.name = name;
        List<SubCategory> newSubcategoriesAssigned = new ArrayList<>();
        newSubcategoriesAssigned.add(subCategory);
        this.subcategoriesAssigned = newSubcategoriesAssigned;
    }

}
