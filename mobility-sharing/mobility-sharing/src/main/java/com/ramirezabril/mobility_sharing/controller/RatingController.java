package com.ramirezabril.mobility_sharing.controller;

import com.ramirezabril.mobility_sharing.model.RatingModel;
import com.ramirezabril.mobility_sharing.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rating")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @GetMapping("/")
    public ResponseEntity<List<RatingModel>> getAllRatings() {
        return ResponseEntity.ok().body(ratingService.retrieveRatings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingModel> getRatingById(@PathVariable int id) {
        return ratingService.retrieveRating(id)
                .map(rating -> ResponseEntity.ok().body(rating))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<RatingModel> createRating(@RequestBody RatingModel ratingModel) {
        return ResponseEntity.ok().body(ratingService.addRating(ratingModel));
    }

    @PutMapping("/")
    public ResponseEntity<RatingModel> updateRating(@RequestBody RatingModel ratingModel) {
        return ratingService.updateRating(ratingModel)
                .map(updatedRating -> ResponseEntity.ok().body(updatedRating))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RatingModel> deleteRating(@PathVariable int id) {
        return ratingService.deleteRating(id)
                .map(deletedRating -> ResponseEntity.ok().body(deletedRating))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/rating-user/{userId}")
    public ResponseEntity<List<RatingModel>> getRatingsByRatingUser(@PathVariable int userId) {
        return ResponseEntity.ok().body(ratingService.retrieveRatingsByRatingUser(userId));
    }

    @GetMapping("/rated-user/{userId}")
    public ResponseEntity<List<RatingModel>> getRatingsByRatedUser(@PathVariable int userId) {
        return ResponseEntity.ok().body(ratingService.retrieveRatingsByRatedUser(userId));
    }

    @GetMapping("/travel/{travelId}")
    public ResponseEntity<List<RatingModel>> getRatingsByTravel(@PathVariable int travelId) {
        return ResponseEntity.ok().body(ratingService.retrieveRatingsByTravel(travelId));
    }
}
