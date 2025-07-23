package Marketplace.dtos.response;

import Marketplace.projections.IListingImageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO de respuesta para una imagen auxiliar de una publicación (listing).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListingImageResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer imgNumber;
    private String imgUrl;

    public static ListingImageResponseDto convertEntityToDto(IListingImageDto dto) {
        return ListingImageResponseDto.builder()
                .imgNumber(dto.getImgNumber())
                .imgUrl(dto.getImgUrl())
                .build();
    }
}