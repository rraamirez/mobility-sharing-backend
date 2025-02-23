package com.ramirezabril.mobility_sharing.controller;

import com.ramirezabril.mobility_sharing.model.UserModel;
import com.ramirezabril.mobility_sharing.service.UserService;
import com.ramirezabril.mobility_sharing.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mobility-sharing-user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenUtil tokenUtil;

    @GetMapping("/me")
    public ResponseEntity<UserModel> getUserByToken(@RequestHeader("Authorization") String authHeader) {
        String token = tokenUtil.extractToken(authHeader);
        return userService.getUserByToken(token)
                .map(user -> ResponseEntity.ok().body(user))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PutMapping("/")
    public ResponseEntity<UserModel> updateUser(
            @RequestBody UserModel userModel,
            @RequestHeader("Authorization") String authHeader) {

        String token = tokenUtil.extractToken(authHeader);
        return userService.updateUser(userModel, token)
                .map(updatedUser -> ResponseEntity.ok().body(updatedUser))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    @PutMapping("/rupee-wallet")
    public ResponseEntity<Void> updateRupeeWallet(
            @RequestParam Integer rupees,
            @RequestParam Integer userId,
            @RequestHeader("Authorization") String authHeader) {

        String token = tokenUtil.extractToken(authHeader);
        userService.updateRupeeWallet(rupees, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id, @RequestHeader("Authorization") String authHeader) {
        String token = tokenUtil.extractToken(authHeader);
        userService.deleteUser(id, token);
        return ResponseEntity.ok().build();
    }
}
