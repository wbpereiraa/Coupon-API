CREATE TABLE coupons (
    id UUID PRIMARY KEY,
    code VARCHAR(6) NOT NULL,
    description VARCHAR(1000) NOT NULL,
    discount_value NUMERIC(10, 2) NOT NULL,
    expiration_date TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    redeemed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_coupons_code ON coupons(code);
CREATE INDEX idx_coupons_status ON coupons(status);
