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
}
