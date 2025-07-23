package Marketplace.controllers;

import Marketplace.commons.constants.TextConstant;
import Marketplace.commons.dtos.ResponseDataDto;
import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.UserRequestDto;
import Marketplace.services.impl.LoginServiceImpl;
import jakarta.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/api/privium/auth")
public class LoginController {

        private static final Logger log = LoggerFactory.getLogger(LoginController.class);
        private static final String LOG_TXT = "LoginController";
        private static final String LOGIN_TXT = "[login]";
        private static final String RESET_TOKEN_TXT = "[setResetToken]";
        private static final String UPDATE_PASSWORD_TXT = "[updatePassword]";

        @Autowired
        private LoginServiceImpl loginService;

        // =======================================================
        // 1) Login de Usuario
        // =======================================================
        @PostMapping(value = "/login", headers = TextConstant.APPLICATION_JSON)
        public ResponseEntity<ResponseDataDto<String>> login(
                        @RequestBody UserRequestDto request) throws SQLException, AuthenticationException {

                log.info(LOG_TXT + LOGIN_TXT + " Iniciando login.");

                ResponseDataDto<String> serviceResponse = loginService.login(request);

                log.info(LOG_TXT + LOGIN_TXT +
                                " Login finalizado");

                return ResponseEntity.ok(serviceResponse);
        }

        // =======================================================
        // 2) Generar Token de Recuperación de Contraseña
        // =======================================================
        @PostMapping(value = "/resetToken", headers = TextConstant.APPLICATION_JSON)
        public ResponseEntity<ResponseDto> setResetToken(
                        @RequestBody UserRequestDto request) throws SQLException, MessagingException {

                log.info(LOG_TXT + RESET_TOKEN_TXT + " Generando token de recuperación.");

                ResponseDto serviceResponse = loginService.setResetToken(request);

                log.info(LOG_TXT + RESET_TOKEN_TXT +
                                " Resultado generación token. Code: {}, Description: {}",
                                serviceResponse.getCode(), serviceResponse.getDescription());

                return ResponseEntity.ok(serviceResponse);
        }

        // =======================================================
        // 3) Actualizar Contraseña
        // =======================================================
        @PostMapping(value = "/updatePassword", headers = TextConstant.APPLICATION_JSON)
        public ResponseEntity<ResponseDto> updatePassword(
                        @RequestBody UserRequestDto request) throws SQLException {

                log.info(LOG_TXT + UPDATE_PASSWORD_TXT + " Actualizando contraseña.");

                ResponseDto serviceResponse = loginService.updatePassword(request);

                log.info(LOG_TXT + UPDATE_PASSWORD_TXT +
                                " Resultado actualización. Code: {}, Description: {}",
                                serviceResponse.getCode(), serviceResponse.getDescription());

                return ResponseEntity.ok(serviceResponse);
        }
}
