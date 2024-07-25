package dev.diego.SupportApp.models;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.diego.SupportApp.controllers.RequestController;
import dev.diego.SupportApp.services.RequestService;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
public class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService service;

    @Autowired
    private ObjectMapper mapper;

    private Request request1;
    private Request request2;

    @BeforeEach
    public void setUp() {
        request1 = new Request();
        request1.setId(1L);
        request1.setRequestName("John Doe");
        request1.setSubject("Subject 1");
        request1.setDescription("Description 1");
        request1.setRequestDate(LocalDateTime.now());

        request2 = new Request();
        request2.setId(2L);
        request2.setRequestName("Jane Doe");
        request2.setSubject("Subject 2");
        request2.setDescription("Description 2");
        request2.setRequestDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should return a list of support requests")
    void testGetAllRequests() throws Exception {
        List<Request> requests = new ArrayList<>();
        requests.add(request1);
        requests.add(request2);

        when(service.getAll()).thenReturn(requests);

        MockHttpServletResponse response = mockMvc.perform(get("/api/support-requests")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        System.out.println(response.getContentAsString());

        assertThat(response.getStatus(), is(200));
        assertThat(response.getContentAsString(), containsString(request1.getRequestName()));
        assertThat(response.getContentAsString(), containsString(request2.getRequestName()));
        assertThat(response.getContentAsString(), equalTo(mapper.writeValueAsString(requests)));
    }

    @Test
    @DisplayName("Should return a single support request by ID")
    void testGetRequestById() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.of(request1));

        MockHttpServletResponse response = mockMvc.perform(get("/api/support-requests/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        System.out.println(response.getContentAsString());

        assertThat(response.getStatus(), is(200));
        assertThat(response.getContentAsString(), containsString(request1.getRequestName()));
        assertThat(response.getContentAsString(), equalTo(mapper.writeValueAsString(request1)));
    }

    @Test
    @DisplayName("Should return 404 when support request by ID is not found")
    void testGetRequestByIdNotFound() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.empty());

        MockHttpServletResponse response = mockMvc.perform(get("/api/support-requests/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();

        assertThat(response.getStatus(), is(404));
    }

    @Test
    @DisplayName("Should create a new support request")
    void testCreateRequest() throws Exception {
        when(service.store(Mockito.any(Request.class))).thenReturn(request1);

        Request newRequest = new Request();
        newRequest.setRequestName("John Doe");
        newRequest.setSubject("Subject 1");
        newRequest.setDescription("Description 1");

        MockHttpServletResponse response = mockMvc.perform(post("/api/support-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        System.out.println(response.getContentAsString());

        assertThat(response.getStatus(), is(201));
        assertThat(response.getContentAsString(), containsString(newRequest.getRequestName()));
        assertThat(response.getContentAsString(), equalTo(mapper.writeValueAsString(request1)));
    }

    @Test
    @DisplayName("Should return 400 for creating a support request with validation errors")
    void testCreateRequestValidationError() throws Exception {
        Request newRequest = new Request(); 

        MockHttpServletResponse response = mockMvc.perform(post("/api/support-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        assertThat(response.getStatus(), is(400));
        assertThat(response.getContentAsString(), containsString("errors"));
    }

    @Test
    @DisplayName("Should update an existing support request")
    void testUpdateRequest() throws Exception {
        when(service.update(Mockito.eq(1L), Mockito.any(Request.class))).thenReturn(request1);

        Request updatedRequest = new Request();
        updatedRequest.setRequestName("John Doe Updated");
        updatedRequest.setSubject("Updated Subject");
        updatedRequest.setDescription("Updated Description");

        MockHttpServletResponse response = mockMvc.perform(put("/api/support-requests/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        System.out.println(response.getContentAsString());

        assertThat(response.getStatus(), is(200));
        assertThat(response.getContentAsString(), containsString(updatedRequest.getRequestName()));
        assertThat(response.getContentAsString(), equalTo(mapper.writeValueAsString(request1)));
    }

    @Test
    @DisplayName("Should return 404 when updating a non-existing support request")
    void testUpdateRequestNotFound() throws Exception {
        when(service.update(Mockito.eq(1L), Mockito.any(Request.class))).thenThrow(new IllegalArgumentException());

        Request updatedRequest = new Request();
        updatedRequest.setRequestName("John Doe Updated");
        updatedRequest.setSubject("Updated Subject");
        updatedRequest.setDescription("Updated Description");

        MockHttpServletResponse response = mockMvc.perform(put("/api/support-requests/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();

        assertThat(response.getStatus(), is(404));
    }

    @Test
    @DisplayName("Should delete an existing support request")
    void testDeleteRequest() throws Exception {
        Mockito.doNothing().when(service).delete(1L);

        MockHttpServletResponse response = mockMvc.perform(delete("/api/support-requests/1"))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse();

        assertThat(response.getStatus(), is(204));
    }

    @Test
    @DisplayName("Should return 404 when deleting a non-existing support request")
    void testDeleteRequestNotFound() throws Exception {
        Mockito.doThrow(new IllegalArgumentException()).when(service).delete(1L);

        MockHttpServletResponse response = mockMvc.perform(delete("/api/support-requests/1"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();

        assertThat(response.getStatus(), is(404));
    }

    @Test
    @DisplayName("Should return the health check status")
    void testHealthCheck() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/api/support-requests/health"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertThat(response.getStatus(), is(200));
        assertThat(response.getContentAsString(), containsString("API is working"));
    }
}