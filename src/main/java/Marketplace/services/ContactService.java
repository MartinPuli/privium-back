package Marketplace.services;

import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.ContactRequestDto;
import jakarta.mail.MessagingException;
import java.sql.SQLException;

public interface ContactService {
    ResponseDto sendContactMessage(Long userId, ContactRequestDto request) throws SQLException, MessagingException;
}
