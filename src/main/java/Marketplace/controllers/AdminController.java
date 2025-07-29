package Marketplace.controllers;

import Marketplace.commons.constants.TextConstant;
import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.ListingAdminRequestDto;
import Marketplace.dtos.request.UserRequestDto;
import Marketplace.services.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/privium/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    private static final String LOG_TXT = "AdminController";
    private static final String APPROVE_TXT = "[approveResidence]";
    private static final String DELETE_LISTING_TXT = "[deleteListing]";

    @Autowired
    private AdminService adminService;


    @PostMapping(value = "/approveResidence", headers = TextConstant.APPLICATION_JSON)
    public ResponseEntity<ResponseDto> approveResidence(
            @RequestHeader(TextConstant.USER_HEADER) Long adminId,
            @RequestBody UserRequestDto request) throws Exception {
        log.info(LOG_TXT + APPROVE_TXT + " Aprobando/rechazando residencia.");
        ResponseDto resp = adminService.approveResidence(adminId, request);
        log.info(LOG_TXT + APPROVE_TXT + " Resultado: {}", resp);
        return ResponseEntity.ok(resp);
    }

    @PostMapping(value = "/deleteListing", headers = TextConstant.APPLICATION_JSON)
    public ResponseEntity<ResponseDto> deleteListing(
            @RequestHeader(TextConstant.USER_HEADER) Long adminId,
            @RequestBody ListingAdminRequestDto request) throws Exception {

        log.info(LOG_TXT + DELETE_LISTING_TXT + " Eliminando listingId={} por admin {}", request.getListingId(), adminId);

        ResponseDto resp = adminService.deleteListing(adminId, request);

        return ResponseEntity.ok(resp);
    }
}
