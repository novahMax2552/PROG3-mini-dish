package com.example.demo.service;

import com.example.demo.entity.Ingredients;
import com.example.demo.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public Ingredients findById(Long id) {
        return ingredientRepository.findById(id);
    }

    public List<Ingredients> findIngredients(int page, int size) {
        return ingredientRepository.findAll(page, size);
    }
}
