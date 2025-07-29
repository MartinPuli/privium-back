package Marketplace.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Marketplace.commons.dtos.ResponseDataDto;
import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.UserRequestDto;
import Marketplace.models.User;
import Marketplace.repositories.IUserCUDRepository;
import Marketplace.repositories.IUserRepository;
import Marketplace.services.UserService;

import java.sql.SQLException;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final String LOG_TXT = "UserService";
    private static final String PIC_TXT = "[manageProfilePicture]";
    private static final String UPDATE_TXT = "[updateUser]";
    private static final String DELETE_TXT = "[deleteUser]";
    private static final String GET_TXT = "[getUserById]";

    @Autowired
    private IUserCUDRepository userCUDRepository;

    @Autowired
    private IUserRepository userRepository;

    @Override
    public ResponseDto manageProfilePicture(Long userId, UserRequestDto request) throws SQLException {
        log.info(LOG_TXT + PIC_TXT +
                 " UserId: {}, profilePicture: {}",
                 userId, request.getProfilePicture());
        return userCUDRepository.manageProfilePicture(
            userId.longValue(),
            request.getProfilePicture()
        );
    }

    @Override
    public ResponseDto updateUser(Long userId, UserRequestDto request) throws SQLException {
        log.info(LOG_TXT + UPDATE_TXT +
                 " UserId: {}, name: {}, lastname: {}, phone: {}",
                 userId, request.getName(), request.getLastname(), request.getPhone());
        return userCUDRepository.updateUser(
            userId.longValue(),
            request.getPhone()
        );
    }

    @Override
    public ResponseDto deleteUser(Long userId) throws SQLException {
        log.info(LOG_TXT + DELETE_TXT + " UserId: {}", userId);
        return userCUDRepository.deleteUser(userId.longValue());
    }

    @Override
    public ResponseDataDto<User> getUserById(Long userId) throws SQLException {
        log.info(LOG_TXT + GET_TXT + " UserId: {}", userId);

        User user = userRepository.findById(userId);

        return ResponseDataDto.<User>builder()
                .code(user != null ? 200 : 404)
                .description(user != null ? "Usuario encontrado" : "Usuario no encontrado")
                .data(user)
                .build();
    }
}
