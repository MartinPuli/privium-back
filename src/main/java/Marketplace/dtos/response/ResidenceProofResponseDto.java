package Marketplace.dtos.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import Marketplace.models.User;
import Marketplace.projections.ICountryDto;
import Marketplace.projections.IResidenceProofDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para una prueba de residencia,
 * incluye datos del usuario y del barrio.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResidenceProofResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;

    // Datos del usuario solicitante
    private String name;
    private String lastName;
    private String dni;
    private String mail;
    
    // Nombre del barrio privado (Country)
    private String countryName;

    // Detalles de la prueba
    private String proofMessage;
    private String proofDocUrl;
    private LocalDateTime createdAt;

    /**
     * Construye el DTO a partir de la proyección y entidad User
     */
    public static ResidenceProofResponseDto fromProjectionAndUser(
            IResidenceProofDto p, User user, ICountryDto country) {
        return ResidenceProofResponseDto.builder()
            .id(p.getId())
            .userId(p.getUserId())
            .name(user.getName())
            .lastName(user.getLastName())
            .dni(user.getDni())
            .mail(user.getEmail())
            .countryName(country.getName())
            .proofMessage(p.getProofMessage())
            .proofDocUrl(p.getProofDocUrl())
            .createdAt(p.getCreatedAt())
            .build();
    }
}
