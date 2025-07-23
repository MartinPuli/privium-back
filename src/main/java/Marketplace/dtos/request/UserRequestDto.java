
package Marketplace.dtos.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto implements Serializable{
    // Para verificar residencia
    private Long idUser;

    // Campos para registro
    private String name;
    private String lastname;
    private String email;
    private String password; // Debe estar hasheada al enviar
    private String dni;
    private String phone;
    private Long countryId;

    private String proofMessage;
    private String proofImageBase64;

    // Para verificación de email/proof
    private String token;
    private Boolean approved;

    // Para reset password
    private String newPassword;


    // Para modificar profile picture
    private String profilePicture;
}
