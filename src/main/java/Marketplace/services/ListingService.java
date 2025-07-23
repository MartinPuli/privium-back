package Marketplace.services;

import Marketplace.dtos.request.ListingRequestDto;
import Marketplace.dtos.response.ListingInfoResponseDto;
import Marketplace.dtos.response.ListingResponseDto;
import Marketplace.models.User;
import Marketplace.commons.dtos.ResponseDataDto;
import Marketplace.dtos.request.ListListingsRequestDto;

import java.sql.SQLException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ListingService {
    ResponseDataDto<ListingInfoResponseDto> getListingInfo(Long listingId) throws SQLException;
    ResponseDataDto<List<ListingResponseDto>> listListings(ListListingsRequestDto request) throws SQLException;
    
    ResponseDataDto<ListingResponseDto> addListingWithImages(
            Long userId,
            ListingRequestDto request,
            MultipartFile mainImage,
            List<MultipartFile> images
    ) throws Exception;

    ResponseDataDto<User> getSeller(ListListingsRequestDto request) throws SQLException;
}
