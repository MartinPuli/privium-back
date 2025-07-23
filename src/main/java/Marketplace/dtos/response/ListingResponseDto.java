package Marketplace.dtos.response;

import Marketplace.projections.IListingDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para una publicación (listing).
 * Incluye campos principales más listas de categorías e imágenes auxiliares.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListingResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Boolean acceptsBarter;
    private Boolean acceptsCash;
    private Boolean acceptsTransfer;
    private Boolean acceptsCard;
    private String type;
    private String brand;
    private Long userId;
    private String mainImage;
    private Integer status;
    private Integer condition;
    private LocalDateTime createdAt;
    private Integer countryId; 

    public static ListingResponseDto convertEntityToDto(
            IListingDto listing
    ) {

        return ListingResponseDto.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .price(listing.getPrice())
                .acceptsBarter(listing.getAcceptsBarter())
                .acceptsCash(listing.getAcceptsCash())
                .acceptsTransfer(listing.getAcceptsTransfer())
                .acceptsCard(listing.getAcceptsCard())
                .type(listing.getType())
                .brand(listing.getBrand())
                .userId(listing.getUserId())
                .mainImage(listing.getMainImage())
                .status(listing.getStatus())
                .condition(listing.getCondition())
                .createdAt(listing.getCreatedAt())
                .countryId(listing.getCountryId())
                .build();
    }
}