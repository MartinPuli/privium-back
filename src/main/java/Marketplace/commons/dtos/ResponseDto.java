
package Marketplace.commons.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity @Data @Builder @AllArgsConstructor @NoArgsConstructor
public class ResponseDto implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	private Integer code;
	private String description;
}
