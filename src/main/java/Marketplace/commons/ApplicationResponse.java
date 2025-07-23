package Marketplace.commons;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonPropertyOrder({"data", "messages"})
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class ApplicationResponse<T> {

    @JsonInclude(Include.NON_NULL)
    private T data;

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty(value = "messages")
    private List<ErrorMessage> errorMessages;

}