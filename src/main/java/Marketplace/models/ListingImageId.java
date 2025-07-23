package Marketplace.models;

import lombok.*;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ListingImageId implements Serializable {
    private Long listingId;
    private Integer imageNumber;
}
