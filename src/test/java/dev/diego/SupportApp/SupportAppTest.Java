package dev.aitor.SupportApp;


import dev.aitor.SupportApp.services.RequestService;
import dev.aitor.SupportApp.repositories.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test") 
public class SupportAppApplicationTests {

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private RequestService requestService;

    @Autowired
    private Environment environment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void contextLoads() {
        // Test to ensure that the Spring application context loads
    }

    @Test
    void testRequestServiceBeanPresence() {
        assertThat(requestService).isNotNull();
    }

       @Test
    void testActiveProfiles() {

        assertThat(environment.getActiveProfiles()).contains("test");
    }

    @Test
    void testRequestRepositoryBeanPresence() {
        assertThat(requestRepository).isNotNull();
    }

}