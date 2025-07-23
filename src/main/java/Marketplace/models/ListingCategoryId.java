package Marketplace.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListingCategoryId implements Serializable {
    private Long listingId;
    private String categoryId;
}
