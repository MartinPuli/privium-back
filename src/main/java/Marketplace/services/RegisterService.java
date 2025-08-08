
package Marketplace.services;

import java.io.IOException;
import java.sql.SQLException;

import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.UserRequestDto;
import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

public interface RegisterService {
    ResponseDto registerUser(UserRequestDto req, MultipartFile document)
            throws SQLException, MessagingException, IOException;
    ResponseDto verifyEmail(UserRequestDto request) throws SQLException, MessagingException;
}
