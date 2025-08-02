package Marketplace.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import Marketplace.commons.dtos.ResponseDataDto;
import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.UserRequestDto;
import Marketplace.models.User;
import Marketplace.dtos.response.UserResponseDto;
import Marketplace.repositories.IListingRepository;
import Marketplace.repositories.IUserCUDRepository;
import Marketplace.repositories.IUserRepository;
import Marketplace.services.S3Service;
import Marketplace.services.UserService;

import java.sql.SQLException;
import java.util.List;

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

    @Autowired
    private IListingRepository listingRepository;

    @Autowired
    private S3Service s3Service;

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
    @Transactional
    public ResponseDto deleteUser(Long userId) throws SQLException {
        log.info(LOG_TXT + DELETE_TXT + " UserId: {}", userId);

        List<String> images = listingRepository.getUserImages(userId);

        ResponseDto resp = userCUDRepository.deleteUser(userId.longValue());

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                for (String url : images) {
                    if (url != null) {
                        try {
                            s3Service.deleteFile(s3Service.extractKey(url));
                        } catch (Exception ex) {
                            log.error("Error deleting user image", ex);
                        }
                    }
                }
            }
        });

        return resp;
    }

    @Override
    public ResponseDataDto<UserResponseDto> getUserById(Long userId) throws SQLException {
        log.info(LOG_TXT + GET_TXT + " UserId: {}", userId);

        User user = userRepository.findById(userId);

        return ResponseDataDto.<UserResponseDto>builder()
                .code(user != null ? 200 : 404)
                .description(user != null ? "Usuario encontrado" : "Usuario no encontrado")
                .data(user != null ? UserResponseDto.fromEntity(user) : null)
                .build();
    }
}
