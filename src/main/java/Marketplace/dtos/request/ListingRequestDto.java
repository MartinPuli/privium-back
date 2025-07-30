package Marketplace.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para crear o editar una publicación (Listing).
 * Incluye los datos principales, además de categorías e imágenes auxiliares.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListingRequestDto implements Serializable {
    // Para edición, se usa listingId
    private Long listingId;

    // Campos de la publicación
    private String title;
    private String description;
    private BigDecimal price;
    private String mainImage;
    private Boolean acceptsBarter;
    private Boolean acceptsCash;
    private Boolean acceptsTransfer;
    private Boolean acceptsCard;
    private String type; // "PRODUCTO" o "SERVICIO"
    private List<String> categoriesId;
    private List<String> imagesUrl;

    private Integer condition;
    private String brand;

    private String action;
}