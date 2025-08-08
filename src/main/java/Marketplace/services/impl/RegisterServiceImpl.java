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

                // 1) Crear usuario
                String hashed = passwordEncoder.encode(req.getPassword());
                User newUser = userRepository.createUser(
                                req.getName(),
                                req.getLastname(),
                                req.getEmail(),
                                hashed,
                                req.getDni(),
                                req.getCountryId(),
                                req.getPhone());

                // 2) Subir proof a S3 PRIVADO si vino archivo
                String proofUrl = null;
                if (document != null && !document.isEmpty()) {
                        String ext = guessExt(document.getOriginalFilename()); // ".jpg", ".pdf", etc.
                        String filename = "proof-" + newUser.getId() + ext;
                        String contentType = document.getContentType() != null
                                        ? document.getContentType()
                                        : "application/octet-stream";
                        proofUrl = s3Service.uploadPrivate(document.getBytes(), filename, contentType);
                }

                // 3) Generar token y guardar todo en email_confirmation_tokens
                String token = UUID.randomUUID().toString();
                authRepository.setEmailConfirmationToken(
                                newUser.getId(),
                                token,
                                req.getProofMessage(),
                                proofUrl);

                // 4) Enviar mail de confirmación
                emailService.sendConfirmationEmail(
                                EmailConfirmationToken.builder()
                                                .token(token)
                                                .user(newUser)
                                                .build());

                // 5) Responder
                return ResponseDto.builder()
                                .code(0)
                                .description("Usuario creado. Revisa tu email para confirmar la cuenta.")
                                .build();
        }

        /** Helper simple para extensión por nombre de archivo */
        private static String guessExt(String name) {
                if (name == null)
                        return "";
                String lower = name.toLowerCase();
                if (lower.endsWith(".jpg") || lower.endsWith(".jpeg"))
                        return ".jpg";
                if (lower.endsWith(".png"))
                        return ".png";
                if (lower.endsWith(".webp"))
                        return ".webp";
                if (lower.endsWith(".pdf"))
                        return ".pdf";
                return ""; // sin extensión conocida
        }

        @Override
        @Transactional
        public ResponseDto verifyEmail(UserRequestDto request) throws SQLException, MessagingException {
                log.info(LOG_TXT + VERIFY_EMAIL_TXT + " Verificando email con token: {}", request.getToken());
                Long userId = authRepository.verifyEmail(request.getToken());
                if (userId != null && userId > 0) {
                        User user = userRepository.findById(userId);
                        emailService.sendPendingVerificationEmail(user);
                        return ResponseDto.builder()
                                        .code(0)
                                        .description("Email verificado correctamente. Quede pendiente a la verificación de la prueba de residencia.")
                                        .build();
                }
                return ResponseDto.builder()
                                .code(1)
                                .description("Token inválido.")
                                .build();
        }
}
