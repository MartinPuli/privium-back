package Marketplace.services.impl;

import Marketplace.commons.dtos.ResponseDataDto;
import Marketplace.dtos.response.CountryResponseDto;
import Marketplace.projections.ICountryDto;
import Marketplace.repositories.ICountryRepository;
import Marketplace.services.CountryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CountryServiceImpl implements CountryService {

    private static final Logger log = LoggerFactory.getLogger(CountryServiceImpl.class);
    private static final String LOG_TXT = "CountryService";
    private static final String GET_TXT = "[getCountries]";

    @Autowired
    private ICountryRepository countryRepository;

    @Override
    @Cacheable("countries")
    public ResponseDataDto<List<CountryResponseDto>> getCountries() throws SQLException {
        log.info(LOG_TXT + GET_TXT + "Obtengo barrios privados");

        // Llamada al repositorio que ejecuta el SP
        List<ICountryDto> raw = countryRepository.getCountries(null);

        // Mapeo a DTOs
        List<CountryResponseDto> data = raw.stream()
            .map(CountryResponseDto::fromProjection)
            .collect(Collectors.toList());

        return ResponseDataDto.<List<CountryResponseDto>>builder()
                .code(200)
                .description("Listado de barrios privados")
                .data(data)
                .build();
    }
}

