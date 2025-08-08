package Marketplace.controllers;

import Marketplace.commons.constants.TextConstant;
import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.UserRequestDto;
import Marketplace.services.impl.RegisterServiceImpl;
import jakarta.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/privium/auth")
public class RegisterController {

        private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
        private static final String LOG_TXT = "RegisterController";
        private static final String REGISTER_TXT = "[registerUser]";
        private static final String VERIFY_EMAIL_TXT = "[verifyEmail]";

        @Autowired
        private RegisterServiceImpl registerService;

        // =======================================================
        // 1) Registro de Usuario
        // =======================================================
        @PostMapping(value = "/register", headers = TextConstant.APPLICATION_JSON)
        public ResponseEntity<ResponseDto> registerUser(
                        @RequestBody UserRequestDto request)
                        throws SQLException, MessagingException, IOException {

                log.info(LOG_TXT + REGISTER_TXT + " Solicitud de registro recibida.");

                ResponseDto serviceResponse = registerService.registerUser(request);

                log.info(LOG_TXT + REGISTER_TXT +
                                " Registro completado. Code: {}, Description: {}",
                                serviceResponse.getCode(), serviceResponse.getDescription());

                return ResponseEntity.ok(serviceResponse);
        }

        // =======================================================
        // 2) Verificación de Email
        // =======================================================
        @PostMapping(value = "/verifyEmail", headers = TextConstant.APPLICATION_JSON)
        public ResponseEntity<ResponseDto> verifyEmail(
                        @RequestBody UserRequestDto request) throws SQLException, MessagingException {

                log.info(LOG_TXT + VERIFY_EMAIL_TXT + " Verificando email.");

                ResponseDto serviceResponse = registerService.verifyEmail(request);

                log.info(LOG_TXT + VERIFY_EMAIL_TXT +
                                " Resultado verificación. Code: {}, Description: {}",
                                serviceResponse.getCode(), serviceResponse.getDescription());

                return ResponseEntity.ok(serviceResponse);
        }
}
