package Marketplace.services.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Marketplace.commons.dtos.ResponseDataDto;
import Marketplace.dtos.response.ResidenceProofResponseDto;
import Marketplace.models.User;
import Marketplace.projections.IResidenceProofDto;
import Marketplace.repositories.IResidenceRepository;
import Marketplace.repositories.IUserRepository;
import Marketplace.services.ResidenceService;

/**
 * Implementación del servicio de gestión de pruebas de residencia.
 * Ahora retorna ResponseDataDto con código y lista de datos.
 */
@Service
public class ResidenceServiceImpl implements ResidenceService {

    private static final Logger log = LoggerFactory.getLogger(ResidenceServiceImpl.class);
    private static final String LOG_TXT = "ResidenceService";
    private static final String GET_PROOFS_TXT = "[getResidenceProofs]";

    @Autowired
    private IResidenceRepository residenceRepository;

    @Autowired
    private IUserRepository userRepository;

    @Override
    public ResponseDataDto<List<ResidenceProofResponseDto>> getResidenceProofs(Long adminId) throws SQLException {
        log.info(LOG_TXT + GET_PROOFS_TXT + "Recuperando pruebas de residencia, userId={}", adminId);

        // 1) Proyecciones desde la base
        List<IResidenceProofDto> projections = residenceRepository.getResidenceProofs(adminId, null);

        // 2) Construcción manual de lista de DTOs
        List<ResidenceProofResponseDto> dtos = new ArrayList<>();
        for (IResidenceProofDto p : projections) {

            User user = userRepository.findById(p.getUserId());

            ResidenceProofResponseDto dto = ResidenceProofResponseDto.fromProjectionAndUser(p, user);
            dtos.add(dto);
        }

        // 3) Envolver en ResponseDataDto
        return ResponseDataDto.<List<ResidenceProofResponseDto>>builder()
                .code(200)
                .data(dtos)
                .build();
    }

}
