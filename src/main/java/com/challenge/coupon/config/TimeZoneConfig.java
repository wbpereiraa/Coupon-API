package com.challenge.coupon.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Configuration
public class TimeZoneConfig {

    private static final ZoneId DISPLAY_ZONE = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer instantDisplayCustomizer() {
        return builder -> builder.serializerByType(Instant.class, new JsonSerializer<Instant>() {
            @Override
            public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers)
                    throws IOException {
                gen.writeString(DISPLAY_FORMATTER.format(value.atZone(DISPLAY_ZONE)));
            }
        });
    }
}
