package com.example.demo.controller;

import com.example.demo.entity.Ingredients;
import com.example.demo.service.IngredientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    // GET /ingredients?page=...&size=...
    @GetMapping
    public List<Ingredients> getIngredients(@RequestParam int page, @RequestParam int size) {
        return ingredientService.findIngredients(page, size);
    }

    // GET /ingredients/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getIngredientById(@PathVariable Long id) {
        Ingredients ing = ingredientService.findById(id);
        if (ing == null) {
            return ResponseEntity.status(404).body("Ingredient.id=" + id + " is not found");
        }
        return ResponseEntity.ok(ing);
    }
}
