package box.reservations.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@NoArgsConstructor
@Data
@Entity
@Table(name = "subcategories")
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Category category;

    public SubCategory(String title, Category category) {
        this.title = title;
        this.category = category;
    }
}
