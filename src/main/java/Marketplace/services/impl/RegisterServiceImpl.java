package Marketplace.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.UserRequestDto;
import Marketplace.models.EmailConfirmationToken;
import Marketplace.models.User;
import Marketplace.repositories.IAuthRepository;
import Marketplace.repositories.IUserRepository;
import Marketplace.services.RegisterService;
import Marketplace.services.S3Service;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.UUID;

@Service
public class RegisterServiceImpl implements RegisterService {

        private static final Logger log = LoggerFactory.getLogger(RegisterServiceImpl.class);
        private static final String LOG_TXT = "RegisterService";
        private static final String REGISTER_TXT = "[registerUser]";
        private static final String VERIFY_EMAIL_TXT = "[verifyEmail]";

        @Autowired
        private IAuthRepository authRepository;

        @Autowired
        private IUserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private EmailServiceImpl emailService;

        @Autowired
        private S3Service s3Service;

        @Override
        @Transactional
        public ResponseDto registerUser(UserRequestDto req, MultipartFile document)
                        throws SQLException, MessagingException, IOException {

                log.info(LOG_TXT + REGISTER_TXT + " Creando usuario para req={}", req);

                if (req.getPassword() == null || req.getPassword().isBlank()) {
                        throw new SQLException("La contraseña es obligatoria");
                }

                // Procesar documento si está presente
                String proofImageBase64 = null;
                byte[] imageBytes = null;
                if (document != null && !document.isEmpty()) {
                        imageBytes = document.getBytes();
                        proofImageBase64 = Base64.getEncoder().encodeToString(imageBytes);
                } else if (req.getProofImageBase64() != null && !req.getProofImageBase64().isBlank()) {
                        String b64 = req.getProofImageBase64();
                        if (b64.contains(","))
                                b64 = b64.split(",", 2)[1];
                        proofImageBase64 = b64;
                        imageBytes = Base64.getDecoder().decode(b64);
                }

                // 1) Crear usuario
                String hashed = passwordEncoder.encode(req.getPassword());
                User newUser = userRepository.createUser(
                                req.getName(), req.getLastname(), req.getEmail(),
                                hashed, req.getDni(), req.getCountryId(), req.getPhone(),
                                req.getProofMessage(), proofImageBase64);

                // 2) Token email
                String token = UUID.randomUUID().toString();
                authRepository.setEmailConfirmationToken(newUser.getId(), token);

                // 3) Mail de confirmación
                emailService.sendConfirmationEmail(
                                EmailConfirmationToken.builder()
                                                .token(token)
                                                .user(newUser)
                                                .build());

                // 4) Guardar prueba en S3 y registrar verificación pendiente
                String filename = null;
                String proofUrl = null;
                if (imageBytes != null) {
                        filename = "proof-" + newUser.getId() + ".jpg";
                        proofUrl = s3Service.uploadProof(imageBytes, filename);
                }

                authRepository.saveVerificationPending(newUser.getId(), req.getProofMessage(), proofUrl);

                // 5) Responder
                return ResponseDto.builder()
                                .code(0)
                                .description("Usuario creado. ")
                                .build();
        }

        @Override
        @Transactional
        public ResponseDto verifyEmail(UserRequestDto request) throws SQLException, MessagingException {
                log.info(LOG_TXT + VERIFY_EMAIL_TXT + " Verificando email con token: {}", request.getToken());
                Long userId = authRepository.verifyEmail(request.getToken());
                if (userId != null && userId > 0) {
                        authRepository.saveVerificationProof(userId);
                        User user = userRepository.findById(userId);
                        emailService.sendPendingVerificationEmail(user);
                        return ResponseDto.builder()
                                        .code(0)
                                        .description("Email verificado.")
                                        .build();
                }
                return ResponseDto.builder()
                                .code(1)
                                .description("Token inválido.")
                                .build();
        }
}
