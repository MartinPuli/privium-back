package Marketplace.repositories;

import Marketplace.commons.dtos.ResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserCUDRepository extends JpaRepository<ResponseDto, Integer> {

    @Query(value = "CALL ManageProfilePicture(:userId, :profilePicture)", nativeQuery = true)
    ResponseDto manageProfilePicture(
        @Param("userId") Long userId,
        @Param("profilePicture") String profilePicture
    );

    @Query(value = "CALL UpdateUser(:userId, :phone)", nativeQuery = true)
    ResponseDto updateUser(
        @Param("userId") Long userId,
        @Param("phone") String phone
    );

    @Query(value = "CALL DeleteUser(:userId)", nativeQuery = true)
    ResponseDto deleteUser(@Param("userId") Long userId);
}
