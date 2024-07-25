package dev.diego.SupportApp.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import dev.diego.SupportApp.models.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {

    
} 
    