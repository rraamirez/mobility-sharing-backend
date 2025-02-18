package com.ramirezabril.mobility_sharing.controller;

import com.ramirezabril.mobility_sharing.model.TravelModel;
import com.ramirezabril.mobility_sharing.model.UserModel;
import com.ramirezabril.mobility_sharing.service.TravelService;
import com.ramirezabril.mobility_sharing.service.UserService;
import com.ramirezabril.mobility_sharing.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<TravelModel> createTravel(@RequestBody TravelModel travelModel, @RequestHeader("Authorization") String authHeader) {
        String token = tokenUtil.extractToken(authHeader);
        UserModel userLogged = null;
        if (token != null) {
            userLogged = userService.getUserByToken(token).get();
        }
        return ResponseEntity.ok().body(travelService.createTravel(travelModel, userLogged));
    }

    @PutMapping("/")
    public ResponseEntity<TravelModel> updateTravel(@RequestBody TravelModel travelModel, @RequestHeader("Authorization") String authHeader) {
        String token = tokenUtil.extractToken(authHeader);
        UserModel userLogged = null;
        if (token != null) {
            userLogged = userService.getUserByToken(token).get();
        }
        return travelService.updateTravel(travelModel, userLogged)
                .map(updatedTravel -> ResponseEntity.ok().body(updatedTravel))
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

    @GetMapping("/origin-destination")
    public ResponseEntity<List<TravelModel>> getTravelsByOriginAndDestination(@RequestParam String origin, @RequestParam String destination, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok().body(travelService.getTravelsByOriginAndDestination(origin, destination));
    }
}
