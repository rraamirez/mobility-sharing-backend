package com.ramirezabril.mobility_sharing.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class HolaMundoController {

    @GetMapping("/alba")
    public ResponseEntity<String> alba() {
        return ResponseEntity.ok("alba");
    }


}
