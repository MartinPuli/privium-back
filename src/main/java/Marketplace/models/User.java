// src/main/java/Marketplace/models/User.java
package Marketplace.models;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "lastname", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(length = 20, unique = true)
    private String dni;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "verified_email", nullable = false)
    private Boolean verifiedEmail = false;

    @Column(name = "verified_residence", nullable = false)
    private Boolean verifiedResidence = false;

    @Column(nullable = false)
    private Integer status = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", foreignKey = @ForeignKey(name = "fk_user_country"))
    private Country country;

    @Column(name = "profile_picture", length = 255)
    private String profilePicture;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "role", nullable = false, length = 20)
    private String role = "USER";

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

