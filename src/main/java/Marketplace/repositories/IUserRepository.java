package Marketplace.repositories;

import Marketplace.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Integer> {
    
    @Query(value = "CALL CreateUser(:name, :lastname, :email, :passwordHash, :dni, :countryId, :phone)", nativeQuery = true)
    User createUser(
        @Param("name") String name,
        @Param("lastname") String lastname,
        @Param("email") String email,
        @Param("passwordHash") String passwordHash,
        @Param("dni") String dni,
        @Param("countryId") Long countryId,
        @Param("phone") String phone
    );

    @Query(value = "CALL GetUserByEmail(:email)", nativeQuery = true)
    Optional<User> findByEmail(@Param("email") String email) throws SQLException;

    @Query(value = "CALL GetUserById(:id)", nativeQuery = true)
    User findById(@Param("id") Long id) throws SQLException;
}
