package Marketplace.dtos.request;

import lombok.*;

import java.io.Serializable;

/**
 * Permite filtrar la rama (rootId) o una hoja exacta (leafId).
 * Si se envía vacío ⇒ se devuelven todas las categorías.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequestDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String rootId;   // ej. "3"
    private String leafId;   // ej. "3>4"
}