package Marketplace.repositories;

import Marketplace.models.ResidenceProof;
import Marketplace.projections.IResidenceProofDto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IResidenceRepository extends JpaRepository<ResidenceProof, Long> {

    @Query(value = "CALL GetResidenceProofs (:userId)", nativeQuery = true)
    List<IResidenceProofDto> getResidenceProofs(@Param("userId") Long userId);
}
