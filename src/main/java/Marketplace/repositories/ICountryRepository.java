package Marketplace.repositories;

import Marketplace.models.Country;
import Marketplace.projections.ICountryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICountryRepository extends JpaRepository<Country, Long> {
    @Query(value = "EXEC dbo.GetCountries :id", nativeQuery = true)
    List<ICountryDto> getCountries(
        @Param("id") Long id
    );
}
