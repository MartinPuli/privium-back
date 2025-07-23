package Marketplace.services;

import Marketplace.commons.dtos.ResponseDto;
import Marketplace.dtos.request.ListingRequestDto;

import java.sql.SQLException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ListingCUDService {
    ResponseDto editListingWithImages(
            Long userId,
            ListingRequestDto request,
            MultipartFile mainImage,
            List<MultipartFile> images) throws Exception;

    ResponseDto manageStatus(ListingRequestDto request) throws SQLException;
}
