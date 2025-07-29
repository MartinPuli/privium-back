package Marketplace.services.impl;

import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.ContactRequestDto;
import Marketplace.models.User;
import Marketplace.repositories.IUserRepository;
import Marketplace.services.ContactService;
import Marketplace.services.EmailService;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.SQLException;

@Service
public class ContactServiceImpl implements ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactServiceImpl.class);
    private static final String LOG_TXT = "ContactService";
    private static final String SEND_TXT = "[sendContactMessage]";

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public ResponseDto sendContactMessage(Long userId, ContactRequestDto request) throws SQLException, MessagingException {
        log.info(LOG_TXT + SEND_TXT + " Enviando mensaje de contacto userId={} msg={}", userId, request.getMessage());

        User user = userRepository.findById(userId);
        emailService.sendContactMessage(user, request.getMessage());

        return ResponseDto.builder()
                .code(200)
                .description("Mensaje enviado correctamente")
                .build();
    }
}
