package Marketplace.dtos.response;

import Marketplace.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO de respuesta para un usuario con informacion basica.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String lastName;
    private String phone;
    private String email;

    public static UserResponseDto fromEntity(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .lastName(user.getLastName())
                .phone(user.getContactPhone())
                .email(user.getEmail())
                .build();
    }
}
