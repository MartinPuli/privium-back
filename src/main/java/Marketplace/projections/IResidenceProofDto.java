package Marketplace.projections;

import java.time.LocalDateTime;

/**
 * Proyección de la tabla residence_proofs para consultas nativas.
 */
public interface IResidenceProofDto {
    Long getId();

    Long getUserId();

    String getProofMessage();

    String getProofImage();

    LocalDateTime getCreatedAt();
}
