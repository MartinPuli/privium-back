package Marketplace.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "listings_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ListingCategoryId.class)
public class ListingCategory {
    @Id
    @Column(name = "product_id")
    private Long listingId;

    @Id
    @Column(name = "category_id", length = 20)
    private String categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Listing listing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;
}
