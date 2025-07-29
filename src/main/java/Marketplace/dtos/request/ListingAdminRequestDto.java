package Marketplace.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListingAdminRequestDto implements Serializable {
    private Long listingId;
    private Long ownerId;
    private String message;
}
