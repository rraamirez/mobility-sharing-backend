package com.ramirezabril.mobility_sharing.auth.controller;

public record RegisterRequest(
        String name,
        String email,
        String password,
        String username,
        Integer rupeeWallet,
        Integer roleId
) {
}
