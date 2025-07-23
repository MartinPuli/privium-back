package Marketplace.services;

import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.UserRequestDto;

import java.sql.SQLException;

public interface UserService {
    ResponseDto manageProfilePicture(Long userId, UserRequestDto request) throws SQLException;
    ResponseDto updateUser(Long userId, UserRequestDto request) throws SQLException;
    ResponseDto deleteUser(Long userId) throws SQLException;
}
