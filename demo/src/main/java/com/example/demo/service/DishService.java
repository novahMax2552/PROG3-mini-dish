package com.example.demo.service;

import com.example.demo.entity.Dish;
import com.example.demo.entity.Ingredients;
import com.example.demo.repository.DishRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class DishService {
    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public Dish findDishById(Long id) {
        return dishRepository.findById(id);
    }

    public void saveDish(Dish dish) {
        dishRepository.save(dish);
    }

    public List<Dish> findAll() {
    return dishRepository.findAll();
}

    public boolean updateIngredients(Long dishId, List<Ingredients> ingredients) {
        return dishRepository.updateIngredients(dishId, ingredients);
    }

}
