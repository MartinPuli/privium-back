package Marketplace.services;

import java.sql.SQLException;
import java.util.List;

import Marketplace.commons.dtos.ResponseDataDto;
import Marketplace.dtos.response.CountryResponseDto;

public interface CountryService {
    ResponseDataDto<List<CountryResponseDto>> getCountries() throws SQLException;
}
