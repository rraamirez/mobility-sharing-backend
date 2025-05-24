package com.ramirezabril.mobility_sharing.util;

import com.ramirezabril.mobility_sharing.repository.UserRepository;
import com.ramirezabril.mobility_sharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SchedulingTasks {
    @Autowired
    private UserService userService;

    @Autowired
    UserRepository userRepository;

    // Scheduled task to credit each user with their calculated weekly rupees
    @Scheduled(cron = "0 0 3 * * MON")
    public void updateUsersRupees() {
        List<Integer> userIds = List.of(1);
        userIds.forEach(userId -> {
            int weeklyRupees = userService.calculateWeeklyRupees(userId);
            userService.computeRupeeWallet(weeklyRupees, userId);
        });
    }

    @Scheduled(cron = "0 0 5 * * *")
    public void updateUsersRatings() {
        userService.computeUserRatings();
    }

    //add ecoranks calculations once a week
    @Scheduled(cron = "0 0 0 * * MON")
    public void updateUsersEcoRanks() {
        userService.computeEcoRanks();
    }
}
