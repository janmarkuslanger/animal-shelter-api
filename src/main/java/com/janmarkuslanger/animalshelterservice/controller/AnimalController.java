package com.janmarkuslanger.animalshelterservice.controller;

import com.janmarkuslanger.animalshelterservice.model.Animal;
import com.janmarkuslanger.animalshelterservice.service.AnimalService;
import com.janmarkuslanger.animalshelterservice.service.VercelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/api/v1/animal", produces = {"application/json"})
public class AnimalController {
    private final AnimalService animalService;
    private final VercelService vercelService;

    public AnimalController(AnimalService animalService, VercelService vercelService) {
        this.animalService = animalService;
        this.vercelService = vercelService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'API')")
    public ResponseEntity<Iterable<Animal>> list() {
        return ResponseEntity.ok(animalService.list());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'API')")
    public Animal get(@PathVariable Long id) {
        return ResponseEntity.ok(animalService.get(id)).getBody();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<Animal> create(@RequestBody Animal animal) {
        Animal newAnimal = new Animal();
        animalService.create(newAnimal);
        vercelService.triggerDeployment();
        return ResponseEntity.ok(newAnimal);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<Animal> update(@PathVariable Long id, @RequestBody Animal animal) {
        Animal updatedAnimal = animalService.get(id);

        if (updatedAnimal == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal not found");
        }
        animalService.update(updatedAnimal, animal);
        vercelService.triggerDeployment();
        return ResponseEntity.ok(updatedAnimal);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        animalService.delete(id);
        vercelService.triggerDeployment();
        return ResponseEntity.ok("Animal deleted");
    }
}