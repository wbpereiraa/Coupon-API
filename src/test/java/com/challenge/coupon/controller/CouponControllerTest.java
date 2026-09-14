package com.challenge.coupon.controller;

import com.challenge.coupon.model.CouponStatus;
import com.challenge.coupon.dto.request.CreateCouponRequest;
import com.challenge.coupon.dto.response.CouponResponse;
import com.challenge.coupon.exception.CouponAlreadyDeletedException;
import com.challenge.coupon.exception.GlobalExceptionHandler;
import com.challenge.coupon.exception.ResourceNotFoundException;
import com.challenge.coupon.service.CouponService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CouponController.class)
@Import(GlobalExceptionHandler.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CouponService couponService;

    private final Instant futureDate = Instant.now().plus(10, ChronoUnit.DAYS);

    @Test
    @DisplayName("POST /coupon - Should create coupon and return 201 Created")
    void shouldCreateCoupon() throws Exception {
        CreateCouponRequest request = CreateCouponRequest.builder()
                .code("ABC-123")
                .description("Desconto promocional")
                .discountValue(0.8)
                .expirationDate(futureDate)
                .published(false)
                .build();

        UUID id = UUID.randomUUID();
        CouponResponse response = CouponResponse.builder()
                .id(id)
                .code("ABC123")
                .description("Desconto promocional")
                .discountValue(0.8)
                .expirationDate(futureDate)
                .status(CouponStatus.ACTIVE)
                .published(false)
                .redeemed(false)
                .build();

        when(couponService.create(any(CreateCouponRequest.class))).thenReturn(response);

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.code").value("ABC123"))
                .andExpect(jsonPath("$.discountValue").value(0.8))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.published").value(false))
                .andExpect(jsonPath("$.redeemed").value(false));
    }

    @Test
    @DisplayName("POST /coupon - Should return 400 Bad Request when validation fails")
    void shouldReturnBadRequestWhenFieldsInvalid() throws Exception {
        CreateCouponRequest invalidRequest = CreateCouponRequest.builder()
                .code("") // Blank code
                .description("Desc")
                .discountValue(0.2) // Less than 0.5
                .expirationDate(Instant.now().minus(1, ChronoUnit.DAYS)) // In past
                .build();

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    @DisplayName("GET /coupon/{id} - Should return 200 OK with coupon data")
    void shouldReturnCouponById() throws Exception {
        UUID id = UUID.randomUUID();
        CouponResponse response = CouponResponse.builder()
                .id(id)
                .code("ABC123")
                .description("Cupom existente")
                .discountValue(15.0)
                .expirationDate(futureDate)
                .status(CouponStatus.ACTIVE)
                .published(true)
                .redeemed(false)
                .build();

        when(couponService.getById(id)).thenReturn(response);

        mockMvc.perform(get("/coupon/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.code").value("ABC123"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.published").value(true));
    }

    @Test
    @DisplayName("GET /coupon/{id} - Should return 404 Not Found when ID does not exist")
    void shouldReturnNotFoundWhenCouponDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(couponService.getById(id)).thenThrow(new ResourceNotFoundException(id));

        mockMvc.perform(get("/coupon/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString(id.toString())));
    }

    @Test
    @DisplayName("DELETE /coupon/{id} - Should return 204 No Content")
    void shouldDeleteCouponSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(couponService).delete(id);

        mockMvc.perform(delete("/coupon/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /coupon/{id} - Should return 400 Bad Request when already deleted")
    void shouldReturnBadRequestWhenCouponAlreadyDeleted() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new CouponAlreadyDeletedException(id)).when(couponService).delete(id);

        mockMvc.perform(delete("/coupon/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("has already been deleted")));
    }
}
