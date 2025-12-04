package com.example.cacex.util.serde;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OffsetDateTimeSerdeTest {

    private ObjectMapper mapper;
    private OffsetDateTimeSerializer serializer;
    private OffsetDateTimeDeserializer deserializer;

    @BeforeEach
    void setup() {
        serializer = new OffsetDateTimeSerializer();
        deserializer = new OffsetDateTimeDeserializer();
        SimpleModule module = new SimpleModule();
        module.addSerializer(OffsetDateTime.class, serializer);
        module.addDeserializer(OffsetDateTime.class, deserializer);

        mapper = new ObjectMapper();
        mapper.registerModule(module);
    }

    @Test
    void roundTripThroughObjectMapper() throws IOException {
        OffsetDateTime original = OffsetDateTime.parse("2025-01-02T03:04:05.678Z");

        String serialized = mapper.writeValueAsString(original);
        assertEquals("\"2025-01-02T03:04:05.678Z\"", serialized);

        OffsetDateTime deserialized = mapper.readValue(serialized, OffsetDateTime.class);
        assertEquals(original, deserialized);
        assertNotNull(deserialized.getOffset());
    }

    @Test
    void serializerWritesNullWhenValueMissing() throws IOException {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = mapper.getFactory().createGenerator(writer);

        serializer.serialize(null, generator, mapper.getSerializerProvider());
        generator.flush();

        assertEquals("null", writer.toString());
    }

    @Test
    void deserializerReturnsNullWhenJsonNull() throws IOException {
        OffsetDateTime parsed = mapper.readValue("null", OffsetDateTime.class);
        assertNull(parsed);
    }
}
