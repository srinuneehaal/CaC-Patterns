package com.example.cacex.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JacksonConfigurationTest {

    private final JacksonConfiguration configuration = new JacksonConfiguration();

    @Test
    void objectMapperRegistersJavaTimeModuleAndExpectedFeatures() {
        ObjectMapper mapper = configuration.objectMapper();

        assertAll("mapper configuration",
                () -> assertFalse(mapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)),
                () -> assertTrue(mapper.isEnabled(SerializationFeature.INDENT_OUTPUT)),
                () -> assertEquals(JsonInclude.Include.NON_NULL,
                        mapper.getSerializationConfig().getDefaultPropertyInclusion().getValueInclusion()),
                () -> assertFalse(mapper.getDeserializationConfig()
                        .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)),
                () -> assertTrue(mapper.getRegisteredModuleIds().stream()
                        .map(Object::toString)
                        .anyMatch(id -> id.contains("jsr310")))
        );
    }

    @Test
    void createObjectMapperProducesFreshMapperWithSameSettings() {
        ObjectMapper first = JacksonConfiguration.createObjectMapper();
        ObjectMapper second = JacksonConfiguration.createObjectMapper();

        assertNotSame(first, second);
        assertFalse(first.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
        assertFalse(first.getDeserializationConfig()
                .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        assertEquals(JsonInclude.Include.NON_NULL,
                first.getSerializationConfig().getDefaultPropertyInclusion().getValueInclusion());
        assertEquals(first.isEnabled(SerializationFeature.INDENT_OUTPUT),
                second.isEnabled(SerializationFeature.INDENT_OUTPUT));
    }
}
