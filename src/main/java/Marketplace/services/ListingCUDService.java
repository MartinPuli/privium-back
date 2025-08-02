package Marketplace.services;

import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.ListingRequestDto;

import java.sql.SQLException;

import org.springframework.web.multipart.MultipartFile;

public interface ListingCUDService {
    ResponseDto editListingWithImages(
            Long userId,
            ListingRequestDto request,
            MultipartFile mainImage,
            MultipartFile image1,
            MultipartFile image2,
            MultipartFile image3,
            MultipartFile image4) throws Exception;

    ResponseDto manageStatus(ListingRequestDto request) throws SQLException;

    /**
     * Elimina una imagen auxiliar de una publicación.
     *
     * @param listingId   id de la publicación
     * @param imageNumber número de imagen (1-4)
     * @return respuesta genérica con el resultado
     */
    ResponseDto deleteListingImage(Long listingId, Integer imageNumber) throws Exception;
}
