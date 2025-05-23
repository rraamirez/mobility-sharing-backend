package com.ramirezabril.mobility_sharing.service.impl;

import com.ramirezabril.mobility_sharing.auth.service.JwtService;
import com.ramirezabril.mobility_sharing.converter.UserConverter;
import com.ramirezabril.mobility_sharing.entity.User;
import com.ramirezabril.mobility_sharing.model.UserModel;
import com.ramirezabril.mobility_sharing.repository.RatingRepository;
import com.ramirezabril.mobility_sharing.repository.TravelRepository;
import com.ramirezabril.mobility_sharing.repository.UserRepository;
import com.ramirezabril.mobility_sharing.repository.UserTravelRepository;
import com.ramirezabril.mobility_sharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    private TravelRepository travelRepository;

    @Autowired
    private UserTravelRepository userTravelRepository;

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

    @Override
    public void computeUserRatings() {
        var userIds = userRepository.getUserIds();
        for (Integer userId : userIds) {
            var ratings = ratingRepository.getRatingsByDriverId(userId);
            if (!ratings.isEmpty()) {
                int rate = ratings.stream().mapToInt(Integer::intValue).sum() / ratings.size();
                if (rate >= 1 && rate <= 5) {
                    userRepository.updateUserRating(userId, rate);
                }
            }
        }
    }

    @Override
    public void computeEcoRanks() {
        List<Integer> userIds = userRepository.getUserIds();

        for (Integer userId : userIds) {
            long score = calculateUserEcoScore(userId);
            int ecoRankId = determineEcoRankId(score);
            userRepository.updateEcoRank(userId, ecoRankId);
        }
    }

    /**
     * Calculates the eco score for a user based on three main factors:
     * - Number of completed travels as a driver
     * - Number of completed recurring travels as a driver (higher weight)
     * - Number of confirmed travels as a passenger
     */
    private long calculateUserEcoScore(Integer userId) {
        long completed = travelRepository.countCompletedTravelsByDriverId(userId).orElse(0L);
        long recurring = travelRepository.countCompletedRecurringTravelsByDriverId(userId).orElse(0L);
        long enrolled = userTravelRepository.countConfirmedUserTravelsByUserId(userId).orElse(0L);

        // Assign weights to each activity type
        return (completed * 5) + (recurring * 8) + (enrolled * 2);
    }

    /**
     * Determines the EcoRank level (1–5) based on the total score.
     * Higher scores reflect greater eco-contributions.
     */
    private int determineEcoRankId(long score) {
        if (score >= 600) return 5;
        if (score >= 300) return 4;
        if (score >= 150) return 3;
        if (score >= 50) return 2;
        return 1;
    }

    private boolean isAdmin(UserModel user) {
        return "ADMIN".equals(user.getRole().getName());
    }
}
