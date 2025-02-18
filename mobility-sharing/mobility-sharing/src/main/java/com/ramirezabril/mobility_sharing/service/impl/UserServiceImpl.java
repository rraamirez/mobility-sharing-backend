package com.ramirezabril.mobility_sharing.service.impl;

import com.ramirezabril.mobility_sharing.auth.service.JwtService;
import com.ramirezabril.mobility_sharing.converter.UserConverter;
import com.ramirezabril.mobility_sharing.model.UserModel;
import com.ramirezabril.mobility_sharing.repository.UserRepository;
import com.ramirezabril.mobility_sharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<UserModel> getUserByToken(String token) {
        var username = jwtService.extractUsername(token);
        return userRepository.findByUsername(username).map(UserConverter::toUserModel);
    }
}
