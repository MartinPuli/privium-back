
package Marketplace.services;

import java.sql.SQLException;

import javax.naming.AuthenticationException;

import Marketplace.commons.dtos.ResponseDataDto;
import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.UserRequestDto;
import jakarta.mail.MessagingException;

public interface LoginService {
    ResponseDataDto<String> login(UserRequestDto request) throws SQLException, AuthenticationException;
    ResponseDto setResetToken(UserRequestDto request) throws SQLException, MessagingException;
    ResponseDto updatePassword(UserRequestDto request) throws SQLException;
}
