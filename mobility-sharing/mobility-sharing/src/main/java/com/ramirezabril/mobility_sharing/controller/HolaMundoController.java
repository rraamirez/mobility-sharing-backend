package com.ramirezabril.mobility_sharing.controller;


import com.ramirezabril.mobility_sharing.model.UserModel;
import com.ramirezabril.mobility_sharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/test")
public class HolaMundoController {
    @Autowired
    private UserService userService;

    @GetMapping("/alba")
    public ResponseEntity<String> alba(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Optional<UserModel> userLogged = userService.getUserByToken(token);
        return ResponseEntity.ok("Usuario autenticado: " + userLogged.get().getUsername());
    }


}
