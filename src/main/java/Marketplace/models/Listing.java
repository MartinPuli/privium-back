package Marketplace.models;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Listing {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*––– Contenido principal –––*/
    @Column(nullable = false, length = 255)
    private String title;

    @Lob
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 20)
    private String type;                 // “PRODUCTO” | “SERVICIO”

    /* Formas de pago */
    private Boolean acceptsBarter   = false;
    private Boolean acceptsCash     = true;
    private Boolean acceptsTransfer = true;
    private Boolean acceptsCard     = false;

    /* Estado publicación */
    private Integer status = 0;         // 0 = nuevo, 1 = activo, -1 = pausado, etc.

    /*––– Relaciones –––*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    @ManyToMany
    @JoinTable(name = "listings_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    /* Multimedia */
    @Column(name = "main_image", columnDefinition = "VARCHAR(MAX)")
    private String mainImage;

    /* Auditoría */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
