package dev.diego.SupportApp.services;

import org.springframework.stereotype.Service;

import dev.diego.SupportApp.models.Request;
import dev.diego.SupportApp.repositories.RequestRepository;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

    private final RequestRepository requestRepository;

    @Autowired
    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public List<Request> getAll() {
        return requestRepository.findAll();
    }

    public Optional<Request> findById(Long id) {
        return requestRepository.findById(id);
    }

    public Request store(@Valid Request request) {
        if (request.getRequestName() == null || request.getRequestName().isBlank()) {
            throw new IllegalArgumentException("Requester name is required");
        }
       
        return requestRepository.save(request);
    }

    public Request update(Long id, @Valid Request updatedRequest) {
        return requestRepository.findById(id)
            .map(existingRequest -> {
                if (updatedRequest.getRequestName() != null) {
                    existingRequest.setRequestName(updatedRequest.getRequestName());
                }
                if (updatedRequest.getSubject() != null) {
                    existingRequest.setSubject(updatedRequest.getSubject());
                }
                if (updatedRequest.getDescription() != null) {
                    existingRequest.setDescription(updatedRequest.getDescription());
                }
               
                return requestRepository.save(existingRequest);
            })
            .orElseThrow(() -> new RuntimeException("Request not found with id " + id));
    }

    public void delete(Long id) {
        if (!requestRepository.existsById(id)) {
            throw new RuntimeException("Request not found with id " + id);
        }
        requestRepository.deleteById(id);
    }
}