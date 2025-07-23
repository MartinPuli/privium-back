package Marketplace.dtos.response;

import Marketplace.projections.ICategoryDto;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private Short hasChild;

    public static CategoryResponseDto fromProjection(ICategoryDto p) {
        return CategoryResponseDto.builder()
                .id(p.getId())
                .name(p.getName())
                .hasChild(p.getHasChild())
                .build();
    }
}
