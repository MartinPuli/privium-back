package Marketplace.models;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "listing_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingImage {
    @EmbeddedId
    private ListingImageId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("listingId")
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @Column(name = "image_url", length = 2048)
    private String imageUrl;
}
