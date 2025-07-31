package Marketplace.services.impl;

import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.ListingAdminRequestDto;
import Marketplace.dtos.request.ListingRequestDto;
import Marketplace.dtos.request.UserRequestDto;
import Marketplace.models.Listing;
import Marketplace.models.User;
import Marketplace.repositories.IAuthRepository;
import Marketplace.repositories.IListingRepository;
import Marketplace.repositories.IUserRepository;
import Marketplace.services.AdminService;
import Marketplace.services.EmailService;
import Marketplace.services.ListingCUDService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);
    private static final String LOG_TXT = "AdminService";
    private static final String APPROVE_TXT = "[approveResidence]";
    private static final String DELETE_TXT = "[deleteListing]";

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IListingRepository listingRepository;
    @Autowired
    private IAuthRepository authRepository;
    @Autowired
    private ListingCUDService listingCUDService;
    @Autowired
    private EmailService emailService;

    private void validateAdmin(Long userId) throws SQLException {
        User u = userRepository.findById(userId);
        if (u == null || !"ADMIN".equalsIgnoreCase(u.getRole())) {
            throw new SQLException("Acceso no autorizado");
        }
    }

    @Override
    @Transactional
    public ResponseDto approveResidence(Long adminId, UserRequestDto req) throws SQLException, MessagingException {
        validateAdmin(adminId);

        log.info(LOG_TXT + APPROVE_TXT + " Aprobando/rechazando residencia");
        ResponseDto dbResp = authRepository.approveResidence(adminId, req.getIdUser(), req.getApproved() ? 1 : 0);

        User user = userRepository.findById(req.getIdUser());
        emailService.sendResidenceDecisionEmail(user, req.getApproved());

        return dbResp;
    }

    @Override
    @Transactional
    public ResponseDto deleteListing(Long adminId, ListingAdminRequestDto req) throws SQLException, MessagingException {
        validateAdmin(adminId);

        log.info(LOG_TXT + DELETE_TXT + " Eliminando listingId={} por admin {}", req.getListingId(), adminId);

        Listing listing = listingRepository.getListingById(req.getListingId());

        User owner = userRepository.findById(req.getOwnerId());

        ListingRequestDto statusReq = new ListingRequestDto();
        statusReq.setListingId(req.getListingId());
        statusReq.setAction("DELETE");
        ResponseDto resp = listingCUDService.manageStatus(statusReq);

        emailService.sendListingDeletionEmail(owner, listing.getTitle(), req.getMessage());

        return resp;
    }
}
