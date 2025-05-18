package com.ramirezabril.mobility_sharing.service.impl;

import com.ramirezabril.mobility_sharing.auth.service.JwtService;
import com.ramirezabril.mobility_sharing.converter.UserConverter;
import com.ramirezabril.mobility_sharing.entity.User;
import com.ramirezabril.mobility_sharing.model.UserModel;
import com.ramirezabril.mobility_sharing.repository.UserRepository;
import com.ramirezabril.mobility_sharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    @Override
    public void updateRupeeWallet(Integer rupees, Integer userId) {
        var user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setId(userId);
            user.setRupeeWallet(rupees);
            userRepository.save(user);
        }
    }

    @Override
    public void computeRupeeWallet(Integer rupees, Integer userId) {
        var user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setId(userId);
            var updateRupeeWallet = user.getRupeeWallet() + (rupees);
            if (updateRupeeWallet < 0) {
                updateRupeeWallet = 0;
            }
            user.setRupeeWallet(updateRupeeWallet);
            userRepository.save(user);
        }
    }

    //private final PasswordEncoder passwordEncoder;

    public Optional<UserModel> updateUser(UserModel user, String token) {
        String username = jwtService.extractUsername(token);
        Optional<UserModel> loggedUserOpt = userRepository.findByUsername(username)
                .map(UserConverter::toUserModel);

        if (!loggedUserOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found or unauthorized");
        }

        UserModel loggedUser = loggedUserOpt.get();

        if (!isUserAuthorizedToUpdate(loggedUser, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own profile");
        }

        updateUserFields(user, loggedUser);

        User updatedEntity = userRepository.save(UserConverter.toUserEntity(loggedUser));

        return Optional.of(UserConverter.toUserModel(updatedEntity));
    }

    private boolean isUserAuthorizedToUpdate(UserModel loggedUser, UserModel user) {
        return loggedUser.getId().equals(user.getId()) || isAdmin(loggedUser);
    }

    private void updateUserFields(UserModel user, UserModel loggedUser) {
        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            loggedUser.setUsername(user.getUsername());
        }
        loggedUser.setEmail(user.getEmail());
        loggedUser.setName(user.getName());
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        loggedUser.setPassword(encoder.encode(user.getPassword()));
    }


    @Override
    public void deleteUser(Integer userId, String token) {
        String username = jwtService.extractUsername(token);
        Optional<UserModel> loggedUserOpt = userRepository.findByUsername(username).map(UserConverter::toUserModel);

        if (loggedUserOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found or unauthorized");
        }

        UserModel loggedUser = loggedUserOpt.get();

        if (!isAdmin(loggedUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this user");
        }

        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    private boolean isAdmin(UserModel user) {
        return "ADMIN".equals(user.getRole().getName());
    }


}
