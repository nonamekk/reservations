package box.reservations.payload.requests.category;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class CategoryNameRequest {
    @NotNull(message = "name must not be null")
    @Length(min = 6, max = 200, message
            = "name must be between 6 and 200 characters")
    private String name;
}
