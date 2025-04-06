package com.ramirezabril.mobility_sharing.controller;

import com.ramirezabril.mobility_sharing.model.TravelModel;
import com.ramirezabril.mobility_sharing.model.UserModel;
import com.ramirezabril.mobility_sharing.service.TravelService;
import com.ramirezabril.mobility_sharing.service.UserService;
import com.ramirezabril.mobility_sharing.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/travel")
public class TravelController {

    @Autowired
    private TravelService travelService;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenUtil tokenUtil;

    @GetMapping("/")
    public ResponseEntity<List<TravelModel>> getAllTravels(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok().body(travelService.getAllTravels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelModel> getTravelById(@PathVariable Integer id, @RequestHeader("Authorization") String authHeader) {
        return travelService.getTravelById(id)
                .map(travel -> ResponseEntity.ok().body(travel))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/")
    public ResponseEntity<TravelModel> createTravel(
            @RequestBody TravelModel travelModel,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenUtil.extractToken(authHeader);
        UserModel userLogged = userService.getUserByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        TravelModel createdTravel = travelService.createTravel(travelModel, userLogged);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTravel);
    }

    @GetMapping("/recurrent-travel")
    public ResponseEntity<List<List<TravelModel>>> getRecurrentTravels(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var recurrentTravelsMatrix = travelService.getRecurringTravels();
        return ResponseEntity.ok().body(recurrentTravelsMatrix);
    }

    @PostMapping("/recurrent-travel")
    public ResponseEntity<List<TravelModel>> createRecurrentTravel(
            @RequestBody List<TravelModel> travelModels,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenUtil.extractToken(authHeader);
        UserModel userLogged = userService.getUserByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        List<TravelModel> createdTravels = travelService.createRecurringTravels(travelModels, userLogged);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTravels);
    }

    @PutMapping("/")
    public ResponseEntity<TravelModel> updateTravel(
            @RequestBody TravelModel travelModel,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenUtil.extractToken(authHeader);
        UserModel userLogged = userService.getUserByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return travelService.updateTravel(travelModel, userLogged)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTravel(@PathVariable Integer id, @RequestHeader("Authorization") String authHeader) {
        //travelService.deleteTravel(id);
        //this method should only be performed by admins panel users, user
        return ResponseEntity.ok().build();
    }

    @PutMapping("/cancel-travel/{id}")
    public ResponseEntity<TravelModel> cancelTravel(@PathVariable Integer id, @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenUtil.extractToken(authHeader);
        UserModel userLogged = userService.getUserByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return ResponseEntity.ok(travelService.cancelTravel(id, userLogged.getId()).get());
    }

    @PutMapping("/complete-travel/{id}")
    public ResponseEntity<TravelModel> completeTravel(@PathVariable Integer id, @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenUtil.extractToken(authHeader);
        UserModel userLogged = userService.getUserByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return ResponseEntity.ok(travelService.completeTravel(id, userLogged.getId()).get());
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<TravelModel>> getTravelsByDriver(@PathVariable Integer driverId, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok().body(travelService.getTravelsByDriver(driverId));
    }

    @GetMapping("/enrolled/{userId}")
    public ResponseEntity<List<TravelModel>> getEnrolledTravelsByUser(@PathVariable Integer userId, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok().body(travelService.getEnrolledTravelsByUser(userId));
    }

    @GetMapping("/origin-destination")
    public ResponseEntity<List<TravelModel>> getTravelsByOriginAndDestination(
            @RequestParam String origin,
            @RequestParam(required = false) String destination,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenUtil.extractToken(authHeader);
        UserModel userLogged = userService.getUserByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        List<TravelModel> travels = travelService.getTravelsByOriginAndDestination(origin, destination, userLogged);
        return ResponseEntity.ok().body(travels);
    }


    @GetMapping("/unratedTravels/{userId}")
    public ResponseEntity<List<TravelModel>> getUnratedTravels(@PathVariable Integer userId, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok().body(travelService.getUnratedTravelsByUser(userId));
    }
}
