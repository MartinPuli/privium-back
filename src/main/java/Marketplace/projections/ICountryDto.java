package Marketplace.projections;

import java.math.BigDecimal;

/**
 * Proyección para los campos de un country,
 * tal como los devuelve el SP getCountries o al mapear entidad Country.
 */
public interface ICountryDto {
    Long       getId();
    String     getName();
    String     getProvince();
    String     getCity();
    String     getPostalCode();
    BigDecimal getLatitude();
    BigDecimal getLongitude();
}
