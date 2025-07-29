package Marketplace.controllers;

import java.sql.SQLException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import Marketplace.commons.constants.TextConstant;
import Marketplace.commons.dtos.ResponseDataDto;
import Marketplace.dtos.response.ResidenceProofResponseDto;
import Marketplace.services.ResidenceService;

/**
 * Controller para endpoints de gestión de pruebas de residencia.
 */
@RestController
//ADMIN
@RequestMapping("/api/privium/residence")
public class ResidenceController {

    private static final Logger log = LoggerFactory.getLogger(ResidenceController.class);
    private static final String LOG_TXT = "ResidenceController";
    private static final String GET_PROOFS_TXT = "[getResidenceProofs]";
    private static final String APPROVE_TXT = "[approveResidence]";

    @Autowired
    private ResidenceService residenceService;


    @PostMapping(value = "/proofs", headers = TextConstant.APPLICATION_JSON)
    public ResponseEntity<ResponseDataDto<List<ResidenceProofResponseDto>>> getResidenceProofs (
            @RequestHeader(TextConstant.USER_HEADER) Long idUser) throws SQLException {

        log.info(LOG_TXT + GET_PROOFS_TXT + " Obteniendo pruebas de residencia.");

        ResponseDataDto<List<ResidenceProofResponseDto>> serviceResponse = residenceService.getResidenceProofs(idUser);

        log.info(LOG_TXT + GET_PROOFS_TXT +
                " Resultado obtención. Code: {}, Description: {}",
                serviceResponse.getCode(), serviceResponse.getDescription());

        return ResponseEntity.ok(serviceResponse);
    }

}
