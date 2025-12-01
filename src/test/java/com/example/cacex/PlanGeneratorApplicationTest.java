package com.example.cacex;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class PlanGeneratorApplicationTest {

    @Test
    void mainDelegatesToSpringApplication() {
        try (MockedStatic<SpringApplication> spring = mockStatic(SpringApplication.class)) {
            String[] args = {"--plan"};
            PlanGeneratorApplication.main(args);
            spring.verify(() -> SpringApplication.run(PlanGeneratorApplication.class, args));
        }
    }

    @Test
    void defaultConstructorLoadsClass() {
        // Covers the implicit default constructor and class initialization.
        new PlanGeneratorApplication();
    }
}
