package com.ramirezabril.mobility_sharing.auth.controller;

public record LoginRequest(
        String username,
        String password
) {
}
