package com.ramirezabril.mobility_sharing.controller;

import com.ramirezabril.mobility_sharing.model.RatingModel;
import com.ramirezabril.mobility_sharing.model.UserModel;
import com.ramirezabril.mobility_sharing.service.RatingService;
import com.ramirezabril.mobility_sharing.service.UserService;
import com.ramirezabril.mobility_sharing.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rating")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenUtil tokenUtil;

    @GetMapping("/")
    public ResponseEntity<List<RatingModel>> getAllRatings(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok().body(ratingService.retrieveRatings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingModel> getRatingById(@PathVariable int id, @RequestHeader("Authorization") String authHeader) {
        return ratingService.retrieveRating(id)
                .map(rating -> ResponseEntity.ok().body(rating))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<RatingModel> createRating(@RequestBody RatingModel ratingModel, @RequestHeader("Authorization") String authHeader) {
        String token = tokenUtil.extractToken(authHeader);
        UserModel userLogged = null;
        if (token != null) {
            userLogged = userService.getUserByToken(token).get();
        }
        return ResponseEntity.ok().body(ratingService.addRating(ratingModel, userLogged));
    }

    @PutMapping("/")
    public ResponseEntity<RatingModel> updateRating(@RequestBody RatingModel ratingModel, @RequestHeader("Authorization") String authHeader) {
        String token = tokenUtil.extractToken(authHeader);
        UserModel userLogged = null;
        if (token != null) {
            userLogged = userService.getUserByToken(token).get();
        }
        return ratingService.updateRating(ratingModel, userLogged)
                .map(updatedRating -> ResponseEntity.ok().body(updatedRating))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RatingModel> deleteRating(@PathVariable int id, @RequestHeader("Authorization") String authHeader) {
        return ratingService.deleteRating(id)
                .map(deletedRating -> ResponseEntity.ok().body(deletedRating))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/rating-user/{userId}")
    public ResponseEntity<List<RatingModel>> getRatingsByRatingUser(@PathVariable int userId, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok().body(ratingService.retrieveRatingsByRatingUser(userId));
    }

    @GetMapping("/rated-user/{userId}")
    public ResponseEntity<List<RatingModel>> getRatingsByRatedUser(@PathVariable int userId, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok().body(ratingService.retrieveRatingsByRatedUser(userId));
    }

    @GetMapping("/travel/{travelId}")
    public ResponseEntity<List<RatingModel>> getRatingsByTravel(@PathVariable int travelId, @RequestHeader("Authorization") String authHeader) {
        String token = tokenUtil.extractToken(authHeader);
        if (token != null) {
            userService.getUserByToken(token);
        }
        return ResponseEntity.ok().body(ratingService.retrieveRatingsByTravel(travelId));
    }
}
