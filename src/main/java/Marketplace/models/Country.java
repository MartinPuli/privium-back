package Marketplace.models;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "countries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Country {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String province;

    @Column(length = 100)
    private String city;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    /* Coordenadas (lat/long)  ── usadas para búsquedas de cercanía */
    private BigDecimal latitude;
    private BigDecimal longitude;
}
