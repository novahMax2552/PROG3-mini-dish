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

    public List<Ingredients> findIngredients(int page, int size) {
        return ingredientRepository.findAll(page, size);
    }

    public void createIngredients(List<Ingredients> newIngredients) {
        ingredientRepository.saveAll(newIngredients);
    }

    public Ingredients findById(Long id) {
    return ingredientRepository.findById(id);
}

public Double findStockValue(Long id, String at, String unit) {
    // Ici tu implémentes la logique JDBC pour récupérer la valeur du stock
    // Exemple : SELECT value FROM stock WHERE ingredient_id=? AND date=? AND unit=?
    return ingredientRepository.findStockValue(id, at, unit);
}

}
