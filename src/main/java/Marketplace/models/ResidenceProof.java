package Marketplace.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity que representa la prueba de residencia de un usuario.
 */
@Entity
@Table(name = "residence_proofs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResidenceProof {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relación con el usuario que subió la prueba.
     * Clave foránea user_id → users.id
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_residence_proof_user")
    )
    private User user;

    @Column(name = "proof_message", length = 500)
    private String proofMessage;

    @Column(name = "proof_image_b64", columnDefinition = "NVARCHAR(MAX)")
    private String proofImageB64;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}