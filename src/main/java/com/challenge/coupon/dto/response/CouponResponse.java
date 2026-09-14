package com.challenge.coupon.dto.response;

import com.challenge.coupon.model.CouponStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representação detalhada do cupom promocional")
public class CouponResponse {

    @Schema(description = "Identificador único (UUID)", example = "cef9d1e3-aae5-4ab6-a297-358c6032b1e7")
    private UUID id;

    @Schema(description = "Código higienizado do cupom (6 caracteres alfanuméricos)", example = "ABC123")
    private String code;

    @Schema(description = "Descrição do cupom", example = "Desconto especial de boas-vindas")
    private String description;

    @Schema(description = "Valor do desconto", example = "0.8")
    private Double discountValue;

    @Schema(description = "Data e hora de expiração", example = "2026-12-31T23:59:59.000Z")
    private Instant expirationDate;

    @Schema(description = "Status do ciclo de vida do cupom", example = "ACTIVE")
    private CouponStatus status;

    @Schema(description = "Indica se o cupom está publicado", example = "false")
    private boolean published;

    @Schema(description = "Indica se o cupom já foi resgatado", example = "false")
    private boolean redeemed;

    public CouponResponse() {
    }

    public CouponResponse(UUID id, String code, String description, Double discountValue, Instant expirationDate, CouponStatus status, boolean published, boolean redeemed) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.status = status;
        this.published = published;
        this.redeemed = redeemed;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(Double discountValue) {
        this.discountValue = discountValue;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Instant expirationDate) {
        this.expirationDate = expirationDate;
    }

    public CouponStatus getStatus() {
        return status;
    }

    public void setStatus(CouponStatus status) {
        this.status = status;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    public void setRedeemed(boolean redeemed) {
        this.redeemed = redeemed;
    }

    public static class Builder {
        private UUID id;
        private String code;
        private String description;
        private Double discountValue;
        private Instant expirationDate;
        private CouponStatus status;
        private boolean published;
        private boolean redeemed;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

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

        public Builder status(CouponStatus status) {
            this.status = status;
            return this;
        }

        public Builder published(boolean published) {
            this.published = published;
            return this;
        }

        public Builder redeemed(boolean redeemed) {
            this.redeemed = redeemed;
            return this;
        }

        public CouponResponse build() {
            return new CouponResponse(id, code, description, discountValue, expirationDate, status, published, redeemed);
        }
    }
}