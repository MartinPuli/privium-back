package Marketplace.projections;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Proyección para los campos de una publicación (listing),
 * tal como los devuelve el SP listListing o al mapear entidad Listing.
 */
public interface IListingDto {
    Long getId();
    String getTitle();
    String getDescription();
    BigDecimal getPrice();
    Boolean getAcceptsBarter();
    Boolean getAcceptsCash();
    Boolean getAcceptsTransfer();
    Boolean getAcceptsCard();
    String getType();
    String getBrand();
    Long getUserId();
    String getMainImage();
    Integer getStatus();
    Integer getCondition();
    LocalDateTime getCreatedAt();
    Integer getCountryId();
}
