package Marketplace.repositories;

import Marketplace.commons.dtos.ResponseDto;

import java.sql.SQLException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IListingCUDRepository extends JpaRepository<ResponseDto, Integer> {

        @Query(value = "EXEC EditListing " +
                        ":listingId, :title, :description, :price, :mainImage, " +
                        ":acceptsBarter, :acceptsCash, :acceptsTransfer, :acceptsCard, " +
                        ":type, :brand, :categoryIds", nativeQuery = true)
        ResponseDto editListing(
                        @Param("listingId") Long listingId,
                        @Param("title") String title,
                        @Param("description") String description,
                        @Param("price") Double price,
                        @Param("mainImage") String mainImage,
                        @Param("acceptsBarter") Boolean acceptsBarter,
                        @Param("acceptsCash") Boolean acceptsCash,
                        @Param("acceptsTransfer") Boolean acceptsTransfer,
                        @Param("acceptsCard") Boolean acceptsCard,
                        @Param("type") String type,
                        @Param("brand") String brand, 
                        @Param("categoryIds") String categoryIds) throws SQLException;

        @Query(value = "EXEC ManageStatus :action, :listingId", nativeQuery = true)
        ResponseDto manageStatus(
                        @Param("action") String action,
                        @Param("listingId") Long listingId);

        @Query(value = "EXEC SetAuxImages :listingId, :images", nativeQuery = true)
        ResponseDto setAuxImages(
                        @Param("listingId") Long listingId,
                        @Param("images") String images);
}
