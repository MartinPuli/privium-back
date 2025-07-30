package Marketplace.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListListingsRequestDto implements Serializable {
    private static final long serialVersionUID = 1L;

    // Búsqueda y paginación
    private Long userId;
    private Integer status;
    private String searchTerm;
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
    private List<String> categoryIds;
    private String sortOrder;      // ASC / DESC
    private Integer page;          // 1-based
    private Integer pageSize;

    // Geolocalización
    private Long countryId;
    private Long centerCountryId;
    private Double maxDistanceKm;

    // Filtros de producto
    private Integer conditionFilter;   // Nuevo nombre para que coincida con @ConditionFilter
    private String  brandFilter;       // Nuevo nombre para que coincida con @BrandFilter
    private Long    listingId;         // Para listar una publicacion puntual
    private Long    notShownListing;   // Para excluir una publicacion específica

    private String type; 

    // NUEVOS: Medios de pago
    private Boolean acceptsBarter;
    private Boolean acceptsCash;
    private Boolean acceptsTransfer;
    private Boolean acceptsCard;

    // NUEVOS: Rango de precio
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    // NUEVO: Excluir publicaciones de un usuario
    private Long notShownUser;
}
