package Marketplace.dtos.response;

import Marketplace.projections.IListingCategoryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO de respuesta para una categoría asociada a una publicación (listing).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListingCategoryResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String categoryId;
    private String description;

    public static ListingCategoryResponseDto convertEntityToDto(IListingCategoryDto dto) {
        return ListingCategoryResponseDto.builder()
                .categoryId(dto.getCategoryId())
                .description(dto.getDescription())
                .build();
    }
}