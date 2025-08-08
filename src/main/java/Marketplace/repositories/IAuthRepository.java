package Marketplace.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import Marketplace.commons.dtos.ResponseDto;

@Repository
public interface IAuthRepository extends JpaRepository<ResponseDto, Integer> {

    @Query(value = "CALL SetEmailConfirmationToken(:userId, :token)", nativeQuery = true)
    ResponseDto setEmailConfirmationToken(@Param("userId") Long userId,
                                          @Param("token") String token);

    @Query(value = "CALL SaveVerificationPending(:userId, :message, :proofUrl)", nativeQuery = true)
    ResponseDto saveVerificationPending(@Param("userId") Long userId,
                                        @Param("message") String message,
                                        @Param("proofUrl") String proofUrl);

    @Query(value = "CALL VerifyEmail(:token)", nativeQuery = true)
    Long verifyEmail(@Param("token") String token);

    @Query(value = "CALL SaveVerificationProof(:userId)", nativeQuery = true)
    ResponseDto saveVerificationProof(@Param("userId") Long userId);

    @Query(value = "CALL SetResetToken(:email, :token)", nativeQuery = true)
    ResponseDto setResetToken(@Param("email") String email,
                              @Param("token") String token);

    @Query(value = "CALL UpdatePassword(:token, :newPasswordHash)", nativeQuery = true)
    ResponseDto updatePassword(@Param("token") String token,
                               @Param("newPasswordHash") String newPasswordHash);

    @Query(value = "CALL ApproveResidence(:adminId, :userId, :approved)", nativeQuery = true)
    ResponseDto approveResidence(@Param("adminId") Long adminId,
                                 @Param("userId") Long userId,
                                 @Param("approved") Integer approved);

    @Query(value = "CALL GetPasswordHashByEmail(:email)", nativeQuery = true)
    String getPasswordHashByEmail(@Param("email") String email);

    @Query(value = "CALL GetPasswordHashByToken(:token)", nativeQuery = true)
    String getPasswordHashByToken(@Param("token") String token);
}
