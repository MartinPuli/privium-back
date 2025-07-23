package Marketplace.commons.dtos;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import Marketplace.commons.ErrorMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Response DTO is an input object with data the result 
 */
@Setter 
@Getter 
@ToString
@EqualsAndHashCode
@Builder 
@AllArgsConstructor 
@NoArgsConstructor
@JsonPropertyOrder({"code", "description", "data", "messages"})
public class ResponseDataDto<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonInclude(Include.NON_NULL)
    private Integer code;

    @JsonInclude(Include.NON_NULL)
    private String description;

    @JsonInclude(Include.NON_NULL)
    private T data;

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty(value = "messages")
    private List<ErrorMessage> errorMessages;
}
