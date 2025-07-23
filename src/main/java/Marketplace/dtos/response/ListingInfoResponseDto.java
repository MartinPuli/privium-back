package Marketplace.dtos.response;

import Marketplace.projections.IListingCategoryDto;
import Marketplace.projections.IListingImageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO de respuesta para las imagenes y categorias de una publicación (listing).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListingInfoResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;


    private List<ListingCategoryResponseDto> categories;
    private List<ListingImageResponseDto> auxiliaryImages;

    public static ListingInfoResponseDto convertEntityToDto(
            List<IListingCategoryDto> categoryDtos,
            List<IListingImageDto> imageDtos
    ) {
        List<ListingCategoryResponseDto> cats = categoryDtos.stream()
                .map(ListingCategoryResponseDto::convertEntityToDto)
                .collect(Collectors.toList());

        List<ListingImageResponseDto> imgs = imageDtos.stream()
                .map(ListingImageResponseDto::convertEntityToDto)
                .collect(Collectors.toList());

        return ListingInfoResponseDto.builder()
                .categories(cats)
                .auxiliaryImages(imgs)
                .build();
    }
}