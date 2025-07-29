package Marketplace.controllers;

import Marketplace.commons.constants.TextConstant;
import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.ContactRequestDto;
import Marketplace.services.ContactService;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/privium/contact")
public class ContactController {

    private static final Logger log = LoggerFactory.getLogger(ContactController.class);
    private static final String LOG_TXT = "ContactController";
    private static final String SEND_TXT = "[sendMessage]";

    @Autowired
    private ContactService contactService;

    @PostMapping(value = "/send", headers = TextConstant.APPLICATION_JSON)
    public ResponseEntity<ResponseDto> sendMessage(
            @RequestHeader(TextConstant.USER_HEADER) Long userId,
            @RequestBody ContactRequestDto request) throws SQLException, MessagingException {

        log.info(LOG_TXT + SEND_TXT + " userId={} msg={}", userId, request.getMessage());

        ResponseDto resp = contactService.sendContactMessage(userId, request);

        log.info(LOG_TXT + SEND_TXT + " Code: {}, Description: {}", resp.getCode(), resp.getDescription());

        return ResponseEntity.ok(resp);
    }
}
