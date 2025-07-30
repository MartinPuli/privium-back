package Marketplace.services;

import Marketplace.commons.dtos.ResponseDataDto;
import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.UserRequestDto;
import Marketplace.models.User;
import Marketplace.dtos.response.UserResponseDto;

import java.sql.SQLException;

public interface UserService {
    ResponseDto manageProfilePicture(Long userId, UserRequestDto request) throws SQLException;
    ResponseDto updateUser(Long userId, UserRequestDto request) throws SQLException;
    ResponseDto deleteUser(Long userId) throws SQLException;
    ResponseDataDto<UserResponseDto> getUserById(Long userId) throws SQLException;
}
