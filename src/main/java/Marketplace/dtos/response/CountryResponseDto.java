package Marketplace.dtos.response;

import Marketplace.projections.ICountryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO de respuesta para un country (barrio privado).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CountryResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String province;
    private String city;
    private String postalCode;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public static CountryResponseDto fromProjection(ICountryDto c) {
        return CountryResponseDto.builder()
                .id(c.getId())
                .name(c.getName())
                .province(c.getProvince())
                .city(c.getCity())
                .postalCode(c.getPostalCode())
                .latitude(c.getLatitude())
                .longitude(c.getLongitude())
                .build();
    }
}