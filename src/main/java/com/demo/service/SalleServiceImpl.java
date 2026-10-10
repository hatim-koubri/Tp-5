package com.demo.service;

import com.demo.entites.Salle;
import com.demo.repository.SalleRepository;
import com.demo.repository.SalleRepositoryImpl;
import com.demo.util.PaginationResult;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class SalleServiceImpl implements SalleService {
    private final SalleRepository salleRepository;

    public SalleServiceImpl(EntityManager entityManager) {
        this.salleRepository = new SalleRepositoryImpl(entityManager);
    }

    @Override
    public List<Salle> getAvailableRooms(LocalDateTime debut, LocalDateTime fin) {
        return salleRepository.findAvailableRooms(debut, fin);
    }

    @Override
    public PaginationResult<Salle> searchRooms(String nom, Integer capaciteMin,
                                               LocalDateTime debut, LocalDateTime fin,
                                               int page, int size) {
        return salleRepository.searchRooms(nom, capaciteMin, debut, fin, page, size);
    }

    @Override
    public long countRooms(String nom, Integer capaciteMin,
                           LocalDateTime debut, LocalDateTime fin) {
        return salleRepository.countRooms(nom, capaciteMin, debut, fin);
    }
}