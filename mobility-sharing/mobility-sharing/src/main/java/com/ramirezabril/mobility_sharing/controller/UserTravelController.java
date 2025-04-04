package com.ramirezabril.mobility_sharing.controller;

import com.ramirezabril.mobility_sharing.model.UserTravelModel;
import com.ramirezabril.mobility_sharing.service.UserService;
import com.ramirezabril.mobility_sharing.service.UserTravelService;
import com.ramirezabril.mobility_sharing.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/user-travel")
public class UserTravelController {

    @Autowired
    private UserTravelService userTravelService;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenUtil tokenUtil;

    @GetMapping("/")
    public ResponseEntity<List<UserTravelModel>> getAllUserTravels(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenUtil.extractToken(authHeader);
        userService.getUserByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return ResponseEntity.ok().body(userTravelService.getAllUserTravels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserTravelModel> getUserTravelById(@PathVariable Integer id, @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenUtil.extractToken(authHeader);
        userService.getUserByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return userTravelService.getUserTravel(id)
                .map(userTravel -> ResponseEntity.ok().body(userTravel))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/") //todo pending by default not sent by frontend and avoid booking twice (must be tested)
    public ResponseEntity<UserTravelModel> createUserTravel(
            @RequestBody UserTravelModel userTravelModel,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenUtil.extractToken(authHeader);
        userService.getUserByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        UserTravelModel createdUserTravel = userTravelService.addUserTravel(userTravelModel)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to create UserTravel"));

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserTravel);
    }

    @PutMapping("/")
    public ResponseEntity<UserTravelModel> updateUserTravel(
            @RequestBody UserTravelModel userTravelModel,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenUtil.extractToken(authHeader);
        userService.getUserByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return userTravelService.updateUserTravel(userTravelModel)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserTravel(@PathVariable Integer id, @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenUtil.extractToken(authHeader);
        userService.getUserByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        UserTravelModel userTravelModel = new UserTravelModel();
        userTravelModel.setId(id);

        userTravelService.deleteUserTravel(userTravelModel);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserTravelModel>> getUserTravelsByUser(@PathVariable Integer userId, @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenUtil.extractToken(authHeader);
        userService.getUserByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return ResponseEntity.ok().body(userTravelService.getUserTravelsByUser(userId));
    }

    @GetMapping("/travel/{travelId}")
    public ResponseEntity<List<UserTravelModel>> getUserTravelsByTravel(@PathVariable Integer travelId, @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = tokenUtil.extractToken(authHeader);
        userService.getUserByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        return ResponseEntity.ok().body(userTravelService.getUserTravelsByTravelId(travelId));
    }
}
