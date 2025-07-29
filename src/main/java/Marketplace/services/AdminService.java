package Marketplace.services;

import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.ListingAdminRequestDto;
import Marketplace.dtos.request.UserRequestDto;
import jakarta.mail.MessagingException;

import java.sql.SQLException;

public interface AdminService {
    ResponseDto approveResidence(Long adminId, UserRequestDto req) throws SQLException, MessagingException;
    ResponseDto deleteListing(Long adminId, ListingAdminRequestDto req) throws Exception;
}
