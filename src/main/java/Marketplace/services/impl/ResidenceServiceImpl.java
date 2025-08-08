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
import Marketplace.models.Country;
import Marketplace.models.User;
import Marketplace.projections.ICountryDto;
import Marketplace.projections.IResidenceProofDto;
import Marketplace.repositories.ICountryRepository;
import Marketplace.repositories.IResidenceRepository;
import Marketplace.repositories.IUserRepository;
import Marketplace.services.ResidenceService;
import Marketplace.services.S3Service;

@Service
public class ResidenceServiceImpl implements ResidenceService {

    private static final Logger log = LoggerFactory.getLogger(ResidenceServiceImpl.class);
    private static final String LOG_TXT = "ResidenceService";
    private static final String GET_PROOFS_TXT = "[getResidenceProofs]";

    @Autowired
    private IResidenceRepository residenceRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ICountryRepository countryRepository;

    @Autowired
    private S3Service s3Service;

    @Override
    public ResponseDataDto<List<ResidenceProofResponseDto>> getResidenceProofs(Long adminId) throws SQLException {
        log.info(LOG_TXT + GET_PROOFS_TXT + " Recuperando pruebas de residencia, userId={}", adminId);

        // 1) Obtener proyecciones desde la base
        List<IResidenceProofDto> projections = residenceRepository.getResidenceProofs(adminId, null);

        // 2) Construir lista de DTOs con presigned URL
        List<ResidenceProofResponseDto> dtos = new ArrayList<>();
        for (IResidenceProofDto p : projections) {

            User user = userRepository.findById(p.getUserId());
            List<ICountryDto> country = countryRepository.getCountries(user.getCountryId());

            ResidenceProofResponseDto dto = ResidenceProofResponseDto.fromProjectionAndUser(p, user, country.get(0));

            // Si tiene archivo en S3, generar URL prefirmada (15 min)
            if (p.getProofDocUrl() != null && !p.getProofDocUrl().isBlank()) {
                String presignedUrl = s3Service.presignGet(p.getProofDocUrl(), 15L);
                dto.setProofDocUrl(presignedUrl);
            }

            dtos.add(dto);
        }

        // 3) Retornar envuelto en ResponseDataDto
        return ResponseDataDto.<List<ResidenceProofResponseDto>>builder()
                .code(200)
                .data(dtos)
                .build();
    }
}
