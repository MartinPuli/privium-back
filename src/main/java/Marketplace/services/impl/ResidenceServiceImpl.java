package Marketplace.services.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Marketplace.commons.dtos.ResponseDataDto;
import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.UserRequestDto;
import Marketplace.dtos.response.ResidenceProofResponseDto;
import Marketplace.models.User;
import Marketplace.projections.IResidenceProofDto;
import Marketplace.repositories.IAuthRepository;
import Marketplace.repositories.IResidenceRepository;
import Marketplace.repositories.IUserRepository;
import Marketplace.services.EmailService;
import Marketplace.services.ResidenceService;
import jakarta.mail.MessagingException;

/**
 * Implementación del servicio de gestión de pruebas de residencia.
 * Ahora retorna ResponseDataDto con código y lista de datos.
 */
@Service
public class ResidenceServiceImpl implements ResidenceService {

    private static final Logger log = LoggerFactory.getLogger(ResidenceServiceImpl.class);
    private static final String LOG_TXT = "ResidenceService";
    private static final String GET_PROOFS_TXT = "[getResidenceProofs]";
    private static final String APPROVE_TXT = "[approveResidence]";

    @Autowired
    private IResidenceRepository residenceRepository;

    @Autowired
    private IAuthRepository authRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public ResponseDataDto<List<ResidenceProofResponseDto>> getResidenceProofs(Long userId) throws SQLException {
        log.info(LOG_TXT + GET_PROOFS_TXT + "Recuperando pruebas de residencia, userId={}", userId);

        // 1) Proyecciones desde la base
        List<IResidenceProofDto> projections = residenceRepository.getResidenceProofs(userId);

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

    @Override
    public ResponseDto approveResidence(Long executorId,
            UserRequestDto req) throws SQLException, MessagingException {

        log.info(LOG_TXT + APPROVE_TXT + "Aprovando prueba de residencia, userId={}", req.getIdUser());

        ResponseDto dbResp = authRepository.approveResidence(
                executorId,
                req.getIdUser(),
                req.getApproved() ? 1 : 0);

        User user = userRepository.findById(req.getIdUser());

        emailService.sendResidenceDecisionEmail(user, req.getApproved());


        log.info(LOG_TXT + APPROVE_TXT + "Prueba de residencia aprobada");
        return dbResp;
    }
}
