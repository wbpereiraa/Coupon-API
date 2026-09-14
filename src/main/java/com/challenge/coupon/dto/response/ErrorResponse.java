package com.challenge.coupon.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Estrutura padrão de resposta para erros")
@Getter
@Setter
public class ErrorResponse {

    @Schema(description = "Momento em que o erro ocorreu", example = "2026-09-12T01:00:00.000Z")
    private Instant timestamp;

    @Schema(description = "Código de status HTTP", example = "400")
    private int status;

    @Schema(description = "Identificação breve do erro HTTP", example = "Bad Request")
    private String error;

    @Schema(description = "Mensagem explicativa do erro", example = "Coupon code must contain exactly 6 alphanumeric characters...")
    private String message;

    @Schema(description = "Caminho do endpoint acessado", example = "/coupon")
    private String path;

    @Schema(description = "Lista opcional de erros detalhados de validação por campo")
    private List<String> fieldErrors;

    public ErrorResponse() {
    }

    public ErrorResponse(Instant timestamp, int status, String error, String message, String path, List<String> fieldErrors) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.fieldErrors = fieldErrors;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Instant timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private List<String> fieldErrors;

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder fieldErrors(List<String> fieldErrors) {
            this.fieldErrors = fieldErrors;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(timestamp, status, error, message, path, fieldErrors);
        }
    }
}