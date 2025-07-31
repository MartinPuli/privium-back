package Marketplace.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.UUID;

import Marketplace.commons.dtos.ResponseDataDto;
import Marketplace.commons.dtos.ResponseDto;
import Marketplace.config.jwt.CustomUserDetails;
import Marketplace.config.jwt.JwtUtil;
import Marketplace.dtos.request.UserRequestDto;
import Marketplace.models.PasswordResetToken;
import Marketplace.models.User;
import Marketplace.repositories.IAuthRepository;
import Marketplace.repositories.IUserRepository;
import Marketplace.services.LoginService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

@Service
public class LoginServiceImpl implements LoginService {

        private static final Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);
        private static final String LOG_TXT = "LoginService";
        private static final String LOGIN_TXT = "[login]";
        private static final String RESET_TOKEN_TXT = "[setResetToken]";
        private static final String UPDATE_PASSWORD_TXT = "[updatePassword]";

        @Autowired
        private IAuthRepository authRepository;

        @Autowired
        private IUserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private EmailServiceImpl emailService;

        @Autowired
        private JwtUtil jwtUtil;

        @Override
        public ResponseDataDto<String> login(UserRequestDto request) throws SQLException, AuthenticationException {
                log.info(LOG_TXT + LOGIN_TXT + " Iniciando login para email: {}", request.getEmail());
                Authentication auth = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getEmail(),
                                                request.getPassword()));

                CustomUserDetails customUser = (CustomUserDetails) auth.getPrincipal();
                User user = customUser.getUser();

                String token = jwtUtil.generateToken(user);

                return ResponseDataDto.<String>builder()
                                .code(200)
                                .description("Login exitoso")
                                .data(token)
                                .build();
        }

        @Override
        @Transactional
        public ResponseDto setResetToken(UserRequestDto request) throws SQLException, MessagingException {
                log.info(LOG_TXT + RESET_TOKEN_TXT +
                                " Generando token de recuperación para email: {}", request.getEmail());

                // 1) Generar token aleatorio
                String resetToken = UUID.randomUUID().toString();

                // 2) Persistir el token en la BD
                ResponseDto resp = authRepository.setResetToken(
                                request.getEmail(),
                                resetToken);

                // 3) Recuperar el usuario completo para poblar el correo
                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new SQLException(
                                                "Token generado pero no se encontró usuario con email: "
                                                                + request.getEmail()));

                // 4) Construir la entidad de token y enviar el correo
                PasswordResetToken tokenEntity = PasswordResetToken.builder()
                                .token(resetToken)
                                .user(user)
                                .build();

                emailService.sendPasswordResetEmail(tokenEntity);

                // 5) Devolver la respuesta original de la SP
                return resp;
        }

        @Override
        @Transactional
        public ResponseDto updatePassword(UserRequestDto request) throws SQLException {
                log.info(LOG_TXT + UPDATE_PASSWORD_TXT + " Validando token: {}", request.getToken());

                String currentHash = authRepository.getPasswordHashByToken(request.getToken());

                if (passwordEncoder.matches(request.getNewPassword(), currentHash)) {
                        throw new SQLException("La nueva contraseña no puede ser igual a la anterior");
                }

                String hashedPassword = passwordEncoder.encode(request.getNewPassword());

                return authRepository.updatePassword(request.getToken(), hashedPassword);
        }

}
