package com.challenge.coupon.exception;

import java.util.UUID;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(UUID id) {
        super(String.format("Coupon with id '%s' was not found.", id));
    }
}
