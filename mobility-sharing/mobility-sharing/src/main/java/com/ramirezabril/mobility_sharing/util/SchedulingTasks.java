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

    //improve logic based on travels
    @Scheduled(cron = "0 0 3 * * MON")
    public void updateUsersRupees() {
        List<Integer> userIds = userRepository.getUserIds();
        userIds.forEach(userId -> userService.updateRupeeWallet(100, userId));
    }

    @Scheduled(cron = "0 0 5 * * *")
    public void updateUsersRatings() {
        userService.computeUserRatings();
    }

    //add ecoranks calculations once a week
    @Scheduled(cron = "0 1 0 * * MON")
    public void updateUsersEcoRanks() {
        userService.computeEcoRanks();
    }
}
