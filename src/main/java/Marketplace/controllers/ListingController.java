package Marketplace.controllers;

import Marketplace.dtos.request.ListingRequestDto;
import Marketplace.commons.constants.TextConstant;
import Marketplace.commons.dtos.ResponseDataDto;
import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.ListListingsRequestDto;
import Marketplace.dtos.response.ListingInfoResponseDto;
import Marketplace.dtos.response.ListingResponseDto;
import Marketplace.models.User;
import Marketplace.services.ListingCUDService;
import Marketplace.services.ListingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/privium/listings")
public class ListingController {

        private static final Logger log = LoggerFactory.getLogger(ListingController.class);
        private static final String LOG_TXT = "ListingController";
        private static final String LIST_TXT = "[listListings]";
        private static final String ADD_TXT = "[addListing]";
        private static final String EDIT_TXT = "[editListing]";
        private static final String STATUS_TXT = "[manageStatus]";

        @Autowired
        private ListingService listingService;

        @Autowired
        private ListingCUDService listingCUDService;

        // =======================================================
        // 1) Obtener información de un producto
        // =======================================================
        @GetMapping("/info/{listingId}")
        public ResponseEntity<ResponseDataDto<ListingInfoResponseDto>> getListingInfo(
                        @RequestHeader(TextConstant.USER_HEADER) Long userId,
                        @PathVariable("listingId") Long listingId) throws SQLException {

                // (Opcional) validar permisos: que userId pueda ver este listing

                ResponseDataDto<ListingInfoResponseDto> resp = listingService.getListingInfo(listingId);

                return ResponseEntity.ok(resp);
        }

        // =======================================================
        // 2) Listar publicaciones con filtros
        // =======================================================
        @PostMapping(value = "/listListings", headers = TextConstant.APPLICATION_JSON)
        public ResponseEntity<ResponseDataDto<List<ListingResponseDto>>> listListings(
                        @RequestHeader(value = TextConstant.USER_HEADER) Long idUser,
                        @RequestBody ListListingsRequestDto request) throws SQLException {

                log.info(LOG_TXT + LIST_TXT + " Inicio consulta de publicaciones");

                ResponseDataDto<List<ListingResponseDto>> serviceResponse = listingService.listListings(request);

                log.info(LOG_TXT + LIST_TXT +
                                " Finaliza consulta de publicaciones");

                return ResponseEntity.ok(serviceResponse);
        }

        // =======================================================
        // 3) Agregar una nueva publicación
        // =======================================================
        @PostMapping(value = "/addListing", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<ResponseDataDto<ListingResponseDto>> addListing(
                        @RequestHeader(TextConstant.USER_HEADER) Long idUser,
                        @RequestPart("data") @Valid ListingRequestDto request,
                        @RequestPart("mainImage") MultipartFile mainImage,
                        @RequestPart(value = "images", required = false) @Size(max = 4, message = "Máximo 4 imágenes auxiliares") List<MultipartFile> images)
                        throws Exception {
                log.info(LOG_TXT + ADD_TXT + " Inicia creación de publicación");
                ResponseDataDto<ListingResponseDto> resp = listingService.addListingWithImages(idUser, request,
                                mainImage, images);
                log.info(LOG_TXT + ADD_TXT + " Finaliza creación de publicación. code={}, desc={}",
                                resp.getCode(), resp.getDescription());
                return ResponseEntity.ok(resp);
        }

        // =======================================================
        // 4) Editar una publicación existente
        // =======================================================
        @PostMapping(value = "/editListing", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<ResponseDto> editListing(
                        @RequestHeader(TextConstant.USER_HEADER) Long idUser,
                        @RequestPart("data") @Valid ListingRequestDto request,
                        @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
                        @RequestPart(value = "images", required = false) @Size(max = 4, message = "Máximo 4 imágenes auxiliares") List<MultipartFile> images)
                        throws Exception {
                log.info(LOG_TXT + EDIT_TXT + " Inicia edición de publicación id={}", request.getListingId());
                ResponseDto resp = listingCUDService.editListingWithImages(idUser, request, mainImage, images);
                log.info(LOG_TXT + EDIT_TXT + " Finaliza edición de publicación. result={}", resp);
                return ResponseEntity.ok(resp);
        }

        // =======================================================
        // 5) Cambiar estado de una publicación (PAUSE|REACTIVATE|DELETE)
        // =======================================================
        @PostMapping(value = "/listingStatus", headers = TextConstant.APPLICATION_JSON)
        public ResponseEntity<ResponseDto> manageStatus(
                        @RequestHeader(value = TextConstant.USER_HEADER) Long idUser,
                        @RequestBody ListingRequestDto request) throws SQLException {

                log.info(LOG_TXT + STATUS_TXT + " Inicio cambio de estado de publicación");

                ResponseDto serviceResponse = listingCUDService.manageStatus(request);

                log.info(LOG_TXT + STATUS_TXT +
                                " Finaliza cambio de estado de publicación");

                return ResponseEntity.ok(serviceResponse);
        }

        // =======================================================
        // 6) Obtener información del vendedor (seller) a partir de filtros
        // =======================================================
        @PostMapping(value = "/getSeller", headers = TextConstant.APPLICATION_JSON)
        public ResponseEntity<ResponseDataDto<User>> getSeller(
                        @RequestHeader(value = TextConstant.USER_HEADER) Long idUser,
                        @RequestBody ListListingsRequestDto request) throws SQLException {

                log.info(LOG_TXT + "[getSeller] Inicio consulta de vendedor");

                // La implementación real dependerá de tu servicio:
                // - puede usar request.getListingId() para traer el seller de un aviso concreto
                // - o bien filtrar por cualquier otro criterio incluido en el DTO
                ResponseDataDto<User> serviceResponse = listingService.getSeller(request);

                log.info(LOG_TXT + "[getSeller] Finaliza consulta de vendedor");

                return ResponseEntity.ok(serviceResponse);
        }
}
