// src/main/java/Marketplace/models/EmailConfirmationToken.java
package Marketplace.models;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_confirmation_tokens", 
        indexes = @Index(name = "idx_email_token", columnList = "token"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String token;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id",
                foreignKey = @ForeignKey(name = "fk_email_token_user"))
    private User user;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
