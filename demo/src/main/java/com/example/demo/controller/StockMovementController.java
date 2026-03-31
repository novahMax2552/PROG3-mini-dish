package com.example.demo.controller;

import com.example.demo.entity.StockMovement;
import com.example.demo.service.StockMovementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/ingredients/{id}/stockMovements")
public class StockMovementController {
    private final StockMovementService service;

    public StockMovementController(StockMovementService service) {
        this.service = service;
    }

    // f) GET /ingredients/{id}/stockMovements?from=...&to=...
    @GetMapping
    public ResponseEntity<?> getStockMovements(@PathVariable Long id,
                                               @RequestParam(required = false) Instant from,
                                               @RequestParam(required = false) Instant to) {
        if (from == null || to == null) {
            return ResponseEntity.status(400).body("Mandatory query parameters `from` and `to` are required.");
        }
        List<StockMovement> movements = service.findByIngredientAndPeriod(id, from, to);
        if (movements.isEmpty()) {
            return ResponseEntity.status(404).body("Ingredient.id=" + id + " is not found");
        }
        return ResponseEntity.ok(movements);
    }

    // g) POST /ingredients/{id}/stockMovements
    @PostMapping
    public ResponseEntity<?> createStockMovements(@PathVariable Long id,
                                                  @RequestBody(required = false) List<StockMovement> newMovements) {
        if (newMovements == null || newMovements.isEmpty()) {
            return ResponseEntity.status(400).body("Request body with stock movements is mandatory.");
        }
        List<StockMovement> saved = service.saveAll(id, newMovements);
        if (saved.isEmpty()) {
            return ResponseEntity.status(404).body("Ingredient.id=" + id + " is not found");
        }
        return ResponseEntity.ok(saved);
    }
}
