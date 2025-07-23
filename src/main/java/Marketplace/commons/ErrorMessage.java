package Marketplace.commons;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import Marketplace.commons.exceptions.BuilderException;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a standard error response structure for the API
 */
@JsonPropertyOrder({ "code", "message", "parameters", "type" })
@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class ErrorMessage {

	private String code;

	private String message;

	private String type;

	@JsonInclude(Include.NON_EMPTY)
	private List<Parameter> parameters;
	
	private static final String CANTCREATE = "Can't create ";

	/**
	 * Builder for the ErrorResponse, to avoid incorrect instantiations
	 */
	public static class Builder {
		private String code;
		private String message;
		private String type;
		private final List<Parameter> parameters = new ArrayList<>();

		public Builder(String code, String message, String type) {
			this.code = code;
			this.message = message;
			this.type = type;
		}

		public Builder withParameter(String parameter) {
			this.parameters.add(new Parameter(parameter));
			return this;
		}

		public ErrorMessage build() {
			if (code == null || code.isEmpty()) {
				String message = CANTCREATE + this.getClass() + ": code cannot be null nor empty";
				throw new BuilderException(message);
			}

			if (message == null || message.isEmpty()) {
				String message = CANTCREATE + this.getClass() + ": message cannot be null nor empty";
				throw new BuilderException(message);
			}

			if (type == null || type.isEmpty()) {
				String message = CANTCREATE + this.getClass() + ": type cannot be null nor empty";
				throw new BuilderException(message);
			}

			if (parameters.stream()
					.anyMatch(parameter -> (parameter.getName() == null || parameter.getName().isEmpty()))) {
				String message = CANTCREATE + this.getClass() + ": present parameters cannot be null nor empty";
				throw new BuilderException(message);
			}

			return new ErrorMessage(code, message, type, parameters);
		}
	}

}