package Marketplace.repositories;

import Marketplace.models.Listing;
import Marketplace.projections.IListingCategoryDto;
import Marketplace.projections.IListingDto;
import Marketplace.projections.IListingImageDto;
import Marketplace.projections.IListingImagesUrlsDto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface IListingRepository extends JpaRepository<Listing, Integer> {

    @Query(value = "CALL AddListing(" +
            ":userId, :title, :description, :brand, :price, :mainImage, " +
            ":condition, :acceptsBarter, :acceptsCash, :acceptsTransfer, :acceptsCard, " +
            ":type, :categoryIds)", nativeQuery = true)
    IListingDto addListing(
            @Param("userId") Long userId,
            @Param("title") String title,
            @Param("description") String description,
            @Param("brand") String brand,
            @Param("price") Double price,
            @Param("mainImage") String mainImage,
            @Param("condition") Integer condition,
            @Param("acceptsBarter") Integer acceptsBarter,
            @Param("acceptsCash") Integer acceptsCash,
            @Param("acceptsTransfer") Integer acceptsTransfer,
            @Param("acceptsCard") Integer acceptsCard,
            @Param("type") String type,
            @Param("categoryIds") String categoryIds) throws SQLException;

    @Query(value = "CALL ListListings(" +
            ":userId, :status, :searchTerm, :createdFrom, :createdTo, :categoryIds, :sortOrder, " +
            ":countryId, :centerCountryId, :maxDistanceKm, :conditionFilter, :brandFilter, :typeFilter, " +
            ":acceptsBarter, :acceptsCash, :acceptsTransfer, :acceptsCard, :minPrice, :maxPrice, :notShownUser, " +
            ":page, :pageSize)", nativeQuery = true)
    List<IListingDto> listListings(
            @Param("userId") Long userId,
            @Param("status") Integer status,
            @Param("searchTerm") String searchTerm,
            @Param("createdFrom") LocalDateTime createdFrom,
            @Param("createdTo") LocalDateTime createdTo,
            @Param("categoryIds") String categoryIds,
            @Param("sortOrder") String sortOrder,
            @Param("countryId") Long countryId,
            @Param("centerCountryId") Long centerCountryId,
            @Param("maxDistanceKm") Double maxDistanceKm,
            @Param("conditionFilter") Integer conditionFilter,
            @Param("brandFilter") String brandFilter,
            @Param("typeFilter") String typeFilter,
            @Param("acceptsBarter") Boolean acceptsBarter,
            @Param("acceptsCash") Boolean acceptsCash,
            @Param("acceptsTransfer") Boolean acceptsTransfer,
            @Param("acceptsCard") Boolean acceptsCard,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("notShownUser") Long notShownUser,
            @Param("page") Integer page,
            @Param("pageSize") Integer pageSize) throws SQLException;

    @Query(value = "CALL GetListingById(:listingId)", nativeQuery = true)
    Listing getListingById(@Param("listingId") Long listingId) throws SQLException;

    @Query(value = "CALL GetAuxImages(:listingId)", nativeQuery = true)
    List<IListingImageDto> getAuxImages(@Param("listingId") Long listingId) throws SQLException;

    @Query(value = "CALL GetListingImages(:listingId)", nativeQuery = true)
    IListingImagesUrlsDto getListingImages(@Param("listingId") Long listingId) throws SQLException;

    @Query(value = "CALL GetListingCategories(:listingId)", nativeQuery = true)
    List<IListingCategoryDto> getListingCategories(@Param("listingId") Long listingId) throws SQLException;
}
