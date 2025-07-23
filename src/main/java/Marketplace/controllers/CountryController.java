package Marketplace.controllers;

import Marketplace.commons.constants.TextConstant;
import Marketplace.commons.dtos.ResponseDataDto;
import Marketplace.dtos.response.CountryResponseDto;
import Marketplace.services.CountryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/privium/countries")
public class CountryController {

    private static final Logger log = LoggerFactory.getLogger(CountryController.class);
    private static final String LOG_TXT = "CountryController";
    private static final String GET_TXT = "[getCountries]";

    @Autowired
    private CountryService countryService;

    /**
     * Devuelve el listado de barrios privados (countries).
     */
    @PostMapping(value = "/getCountries", headers = TextConstant.APPLICATION_JSON)
    public ResponseEntity<ResponseDataDto<List<CountryResponseDto>>> getCountries() throws SQLException {
        log.info(LOG_TXT + GET_TXT + " Inicio recuperación de countries");

        ResponseDataDto<List<CountryResponseDto>> serviceResponse = countryService.getCountries();

        log.info(LOG_TXT + GET_TXT + " Finaliza recuperación de countries");
        return ResponseEntity.ok(serviceResponse);
    }
}
