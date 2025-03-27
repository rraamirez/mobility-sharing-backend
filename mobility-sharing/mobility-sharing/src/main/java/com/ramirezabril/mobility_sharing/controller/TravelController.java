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
        travelService.deleteTravel(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<TravelModel>> getTravelsByDriver(@PathVariable Integer driverId, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok().body(travelService.getTravelsByDriver(driverId));
    }

    @GetMapping("/origin-destination") //it will return only whose driver is not the one that is finding a trip
    public ResponseEntity<List<TravelModel>> getTravelsByOriginAndDestination(@RequestParam String origin, @RequestParam String destination, @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenUtil.extractToken(authHeader);
        UserModel userLogged = userService.getUserByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));


        return ResponseEntity.ok().body(travelService.getTravelsByOriginAndDestination(origin, destination, userLogged));
    }

    @GetMapping("/unratedTravels/{userId}")
    public ResponseEntity<List<TravelModel>> getUnratedTravels(@PathVariable Integer userId, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok().body(travelService.getUnratedTravelsByUser(userId));
    }
}
