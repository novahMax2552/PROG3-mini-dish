package com.example.demo.service;

import com.example.demo.entity.StockMovement;
import com.example.demo.repository.StockMovementRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class StockMovementService {
    private final StockMovementRepository repository;

    public StockMovementService(StockMovementRepository repository) {
        this.repository = repository;
    }

    public List<StockMovement> findByIngredientAndPeriod(Long ingredientId, Instant from, Instant to) {
        return repository.findByIngredientAndPeriod(ingredientId, from, to);
    }

    public List<StockMovement> saveAll(Long ingredientId, List<StockMovement> newMovements) {
        return repository.saveAll(ingredientId, newMovements);
    }
}
