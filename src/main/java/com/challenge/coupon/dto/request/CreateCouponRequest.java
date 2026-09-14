package com.challenge.coupon.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Schema(description = "Payload para criação de cupom promocional")
@Getter
@Setter
public class CreateCouponRequest {

    @NotBlank(message = "Coupon code is required and cannot be blank.")
    @Schema(description = "Código do cupom (alfanumérico com 6 caracteres após higienização)", example = "ABC-123")
    private String code;

    @NotBlank(message = "Coupon description is required and cannot be blank.")
    @Schema(description = "Descrição detalhada do cupom", example = "Desconto especial de boas-vindas")
    private String description;

    @NotNull(message = "Discount value is required.")
    @DecimalMin(value = "0.5", message = "Discount value must be at least 0.5.")
    @Schema(description = "Valor do desconto (mínimo 0.5)", example = "0.8")
    private Double discountValue;

    @NotNull(message = "Expiration date is required.")
    @Future(message = "Expiration date must be in the future.")
    @Schema(description = "Data e hora de expiração no formato ISO-8601", example = "2026-12-31T23:59:59.000Z")
    private Instant expirationDate;

    @Schema(description = "Indica se o cupom já nasce publicado", example = "false", defaultValue = "false")
    private Boolean published = false;

    public CreateCouponRequest() {
    }

    public CreateCouponRequest(String code, String description, Double discountValue, Instant expirationDate, Boolean published) {
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.published = (published != null) ? published : false;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String code;
        private String description;
        private Double discountValue;
        private Instant expirationDate;
        private Boolean published = false;

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder discountValue(Double discountValue) {
            this.discountValue = discountValue;
            return this;
        }

        public Builder expirationDate(Instant expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public Builder published(Boolean published) {
            this.published = published;
            return this;
        }

        public CreateCouponRequest build() {
            return new CreateCouponRequest(code, description, discountValue, expirationDate, published);
        }
    }
}