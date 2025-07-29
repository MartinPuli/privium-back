package Marketplace.controllers;

import Marketplace.commons.constants.TextConstant;
import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.UserRequestDto;
import Marketplace.dtos.request.ListingAdminRequestDto;
import Marketplace.dtos.request.ListingRequestDto;
import Marketplace.models.Listing;
import Marketplace.models.User;
import Marketplace.repositories.IListingRepository;
import Marketplace.repositories.IUserRepository;
import Marketplace.services.EmailService;
import Marketplace.services.ListingCUDService;
import Marketplace.services.ResidenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.mail.MessagingException;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/privium/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private static final String LOG_TXT = "AdminController";
    private static final String APPROVE_TXT = "[approveResidence]";
    private static final String DELETE_LISTING_TXT = "[deleteListing]";

    @Autowired
    private ResidenceService residenceService;

    @Autowired
    private ListingCUDService listingCUDService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IListingRepository listingRepository;

    @Autowired
    private EmailService emailService;

    private void validateAdmin(Long userId) throws SQLException {
        User u = userRepository.findById(userId);
        if (u == null || !"ADMIN".equalsIgnoreCase(u.getRole())) {
            throw new SQLException("Acceso no autorizado");
        }
    }

    @PostMapping(value = "/approveResidence", headers = TextConstant.APPLICATION_JSON)
    public ResponseEntity<ResponseDto> approveResidence(
            @RequestHeader(TextConstant.USER_HEADER) Long adminId,
            @RequestBody UserRequestDto request) throws SQLException, MessagingException {

        validateAdmin(adminId);

        log.info(LOG_TXT + APPROVE_TXT + " Aprobando/rechazando residencia.");
        ResponseDto resp = residenceService.approveResidence(adminId, request);
        log.info(LOG_TXT + APPROVE_TXT + " Resultado: {}", resp);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/deleteListing", headers = TextConstant.APPLICATION_JSON)
    public ResponseEntity<ResponseDto> deleteListing(
            @RequestHeader(TextConstant.USER_HEADER) Long adminId,
            @RequestBody ListingAdminRequestDto request) throws Exception {

        validateAdmin(adminId);

        log.info(LOG_TXT + DELETE_LISTING_TXT + " Eliminando listingId={} por admin {}", request.getListingId(), adminId);

        Listing listing = listingRepository.findById(request.getListingId()).orElse(null);
        if (listing == null) {
            return ResponseEntity.ok(ResponseDto.builder().code(404).description("Publicación no encontrada").build());
        }

        ListingRequestDto statusReq = new ListingRequestDto();
        statusReq.setListingId(request.getListingId());
        statusReq.setAction("DELETE");
        ResponseDto resp = listingCUDService.manageStatus(statusReq);

        if (resp != null && (resp.getCode() == 0 || resp.getCode() == 200)) {
            try {
                emailService.sendListingDeletionEmail(listing.getOwner(), listing.getTitle(), request.getMessage());
            } catch (MessagingException e) {
                log.error(LOG_TXT + DELETE_LISTING_TXT + " Error enviando correo", e);
            }
        }

        return ResponseEntity.ok(resp);
    }
}
