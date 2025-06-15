package com.ramirezabril.mobility_sharing.service.impl;

import com.ramirezabril.mobility_sharing.auth.service.JwtService;
import com.ramirezabril.mobility_sharing.converter.UserConverter;
import com.ramirezabril.mobility_sharing.entity.User;
import com.ramirezabril.mobility_sharing.model.UserModel;
import com.ramirezabril.mobility_sharing.model.WeeklyEnvironmentalStatsDTO;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Service("userService")
public class UserServiceImpl implements UserService {

    private static final int RUPEES_PER_COMPLETED_TRIP = 6;
    private static final int RUPEES_PER_RECURRING_TRIP = 8;
    private static final int RUPEES_PER_CONFIRMED_RIDE = 4;

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

    private boolean isAdmin(UserModel user) {
        return "ADMIN".equals(user.getRole().getName());
    }

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
        return (completed * RUPEES_PER_COMPLETED_TRIP) + (recurring * RUPEES_PER_RECURRING_TRIP) + (enrolled * RUPEES_PER_CONFIRMED_RIDE);
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

    @Override
    public int calculateWeeklyRupees(Integer userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusWeeks(1);

        long totalConfirmedPassengers = userTravelRepository
                .countUserTravelsForDriverByDateBetween(userId, weekAgo, today)
                .orElse(0L);

        long completed = travelRepository
                .countCompletedTravelsByDriverIdAndDateBetween(userId, weekAgo, today)
                .orElse(0L);

        long recurring = travelRepository
                .countCompletedRecurringTravelsByDriverIdAndDateBetween(userId, weekAgo, today)
                .orElse(0L);

        long confirmed = userTravelRepository
                .countConfirmedUserTravelsByUserIdAndDateBetween(userId, weekAgo, today)
                .orElse(0L);

        int baseWeeklyReward = (int) (
                completed * RUPEES_PER_COMPLETED_TRIP +
                        recurring * RUPEES_PER_RECURRING_TRIP +
                        confirmed * RUPEES_PER_CONFIRMED_RIDE
        );

        // Bonus: if driver confirmed passengers are at least double the completed trips,
        // apply a 20% bonus on base reward
        double efficiencyBonus = 0;
        if (completed > 0 && totalConfirmedPassengers >= 2 * completed) {
            efficiencyBonus = baseWeeklyReward * 0.2;
        }

        final double REWARD_MULTIPLIER = 3;

        long finalReward = Math.round((baseWeeklyReward + efficiencyBonus) * REWARD_MULTIPLIER);
        return (int) Math.min(finalReward, Integer.MAX_VALUE);
    }


    @Override
    public WeeklyEnvironmentalStatsDTO getWeeklyEnvironmentalStats(Integer userId) {
        LocalDate startOfWeek = getStartOfWeek();
        LocalDate endOfWeek = getEndOfWeek();

        long completedTrips = getCompletedTripsThisWeek(userId, startOfWeek, endOfWeek);
        long confirmedRides = getConfirmedRidesThisWeek(userId, startOfWeek, endOfWeek);
        long passengersThisWeek = getConfirmedPassengersThisWeek(userId, startOfWeek, endOfWeek);
        int weeklyRupees = calculateWeeklyRupees(userId);

        double avgPassengers = completedTrips > 0
                ? (double) passengersThisWeek / completedTrips
                : 0.0;

        double co2SavedKg = calculateCo2Saved(completedTrips, confirmedRides, passengersThisWeek);
        double co2SavedKgTotal = calculateTotalCo2Saved(userId);

        return WeeklyEnvironmentalStatsDTO.builder()
                .averagePassengersPerCompletedTrip(avgPassengers)
                .confirmedRides((int) confirmedRides)
                .weeklyRupees(weeklyRupees)
                .co2SavedKg(co2SavedKg)
                .co2SavedKgTotal(co2SavedKgTotal)
                .build();
    }


    private LocalDate getStartOfWeek() {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private LocalDate getEndOfWeek() {
        return LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    private long getCompletedTripsThisWeek(Integer userId, LocalDate start, LocalDate end) {
        return travelRepository.countCompletedTravelsByDriverIdAndDateBetween(userId, start, end).orElse(0L);
    }

    private long getConfirmedRidesThisWeek(Integer userId, LocalDate start, LocalDate end) {
        return userTravelRepository.countConfirmedUserTravelsByUserIdAndDateBetween(userId, start, end).orElse(0L);
    }

    private long getConfirmedPassengersThisWeek(Integer userId, LocalDate start, LocalDate end) {
        return userTravelRepository.countUserTravelsForDriverByDateBetween(userId, start, end).orElse(0L);
    }

    private double calculateCo2Saved(long completedTrips, long confirmedRides, long confirmedPassengers) {
        final double AVG_KM_PER_TRIP = 10.0;
        final double CO2_PER_KM = 0.21;
        return (completedTrips + confirmedRides + confirmedPassengers) * AVG_KM_PER_TRIP * CO2_PER_KM;
    }


    private double calculateTotalCo2Saved(Integer userId) {
        final double AVG_KM_PER_TRIP = 10.0;
        final double CO2_PER_KM = 0.21;

        long totalCompletedTrips = travelRepository
                .countCompletedTravelsByDriverId(userId)
                .orElse(0L);

        long totalConfirmedRides = userTravelRepository
                .countConfirmedUserTravelsByUserId(userId)
                .orElse(0L);

        long totalPassengersAsDriver = userTravelRepository
                .countAllConfirmedPassengersForDriver(userId)
                .orElse(0L);

        long totalContributions = totalCompletedTrips + totalConfirmedRides + totalPassengersAsDriver;

        return totalContributions * AVG_KM_PER_TRIP * CO2_PER_KM;
    }
}
