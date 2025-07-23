package Marketplace.controllers;

import Marketplace.commons.constants.TextConstant;
import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.UserRequestDto;
import Marketplace.services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/privium/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private static final String LOG_TXT = "UserController";
    private static final String PIC_TXT = "[manageProfilePicture]";
    private static final String UPDATE_TXT = "[updateUser]";
    private static final String DELETE_TXT = "[deleteUser]";

    @Autowired
    private UserService userService;

    @PostMapping(value = "/modifyPicture", headers = TextConstant.APPLICATION_JSON)
    public ResponseEntity<ResponseDto> modifyProfilePicture(
            @RequestHeader(TextConstant.USER_HEADER) Long idUser,
            @RequestBody UserRequestDto request) throws SQLException {

        log.info(LOG_TXT + PIC_TXT + "Actualizando foto de perfil del usuario",
                 idUser, request.getProfilePicture());

        ResponseDto resp = userService.manageProfilePicture(idUser, request);

        log.info(LOG_TXT + PIC_TXT +
                 " Code: {}, Description: {}",
                 resp.getCode(), resp.getDescription());

        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/update", headers = TextConstant.APPLICATION_JSON)
    public ResponseEntity<ResponseDto> updateUser(
            @RequestHeader(TextConstant.USER_HEADER) Long idUser,
            @RequestBody UserRequestDto request) throws SQLException {

        log.info(LOG_TXT + UPDATE_TXT + "Actualizando usuario",
                 idUser, request.getName(), request.getLastname(), request.getPhone());

        ResponseDto resp = userService.updateUser(idUser, request);

        log.info(LOG_TXT + UPDATE_TXT +
                 " Code: {}, Description: {}",
                 resp.getCode(), resp.getDescription());

        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/delete", headers = TextConstant.APPLICATION_JSON)
    public ResponseEntity<ResponseDto> deleteUser(
            @RequestHeader(TextConstant.USER_HEADER) Long idUser) throws SQLException {

        log.info(LOG_TXT + DELETE_TXT + " Eliminando usuario", idUser);

        ResponseDto resp = userService.deleteUser(idUser);

        log.info(LOG_TXT + DELETE_TXT +
                 " Code: {}, Description: {}",
                 resp.getCode(), resp.getDescription());

        return ResponseEntity.ok(resp);
    }
}
