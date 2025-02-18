package com.ramirezabril.mobility_sharing.service;

import com.ramirezabril.mobility_sharing.model.UserModel;

import java.util.Optional;

public interface UserService {
    Optional<UserModel> getUserByToken(String token);
}
