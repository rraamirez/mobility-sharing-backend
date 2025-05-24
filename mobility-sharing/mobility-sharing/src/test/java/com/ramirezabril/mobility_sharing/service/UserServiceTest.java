package com.ramirezabril.mobility_sharing.service;

import com.ramirezabril.mobility_sharing.auth.service.JwtService;
import com.ramirezabril.mobility_sharing.converter.UserConverter;
import com.ramirezabril.mobility_sharing.entity.Role;
import com.ramirezabril.mobility_sharing.entity.User;
import com.ramirezabril.mobility_sharing.model.RoleModel;
import com.ramirezabril.mobility_sharing.model.UserModel;
import com.ramirezabril.mobility_sharing.model.WeeklyEnvironmentalStatsDTO;
import com.ramirezabril.mobility_sharing.repository.TravelRepository;
import com.ramirezabril.mobility_sharing.repository.UserRepository;
import com.ramirezabril.mobility_sharing.repository.UserTravelRepository;
import com.ramirezabril.mobility_sharing.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TravelRepository travelRepository;

    @Mock
    private UserTravelRepository userTravelRepository;

    @Mock
    private UserConverter userConverter;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserModel userModel;
    private UserModel userModel2;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl();
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setRupeeWallet(100);
        user.setRole(new Role(1, "ADMIN"));


        userModel = new UserModel();
        userModel.setId(1);
        userModel.setUsername("testuser");
        userModel.setRupeeWallet(100);
        userModel.setRole(new RoleModel(1, "ADMIN"));

        userModel2 = new UserModel();
        userModel2.setId(2);
        userModel2.setUsername("testuser2");
        userModel2.setRupeeWallet(1002);
        userModel2.setRole(new RoleModel(2, "USER"));
    }

    @Test
    void testGetUserByToken_ShouldReturnUserModel() {
        String token = "valid_token";
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<UserModel> result = userService.getUserByToken(token);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void testUpdateRupeeWallet_ShouldUpdateWallet() {
        Integer newRupees = 200;
        Integer userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.updateRupeeWallet(newRupees, userId);

        verify(userRepository, times(1)).save(user);
        assertEquals(200, user.getRupeeWallet());
    }

    @Test
    void testUpdateUser_ShouldUpdateUserWhenAuthorized() {
        String token = "valid_token";
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        userModel.setPassword("password");

        Optional<UserModel> result = userService.updateUser(userModel, token);

        assertTrue(result.isPresent());
        assertEquals(userModel.getUsername(), result.get().getUsername());
    }

    @Test
    void testUpdateUser_ShouldThrowForbiddenWhenNotAuthorized() {
        String token = "valid_token";
        when(jwtService.extractUsername(token)).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        user.setRole(new Role(1, "USER"));

        UserModel anotherUserModel = new UserModel();
        anotherUserModel.setId(2);
        anotherUserModel.setUsername("anotheruser");
        anotherUserModel.setRole(new RoleModel(2, "USER"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                userService.updateUser(anotherUserModel, token));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("You can only update your own profile", exception.getReason());
    }

    @Test
    void testDeleteUser_ShouldDeleteUserWhenAdmin() {
        Integer userId = 1;
        String token = "admin_token";
        UserModel adminUser = new UserModel();
        adminUser.setId(1);
        adminUser.setRole(new RoleModel(1, "ADMIN"));

        when(jwtService.extractUsername(token)).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(UserConverter.toUserEntity(userModel)));
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId, token);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteUser_ShouldThrowForbiddenWhenNotAdmin() {
        Integer userId = 1;
        String token = "user_token";
        UserModel regularUser = new UserModel();
        regularUser.setId(2);
        regularUser.setRole(new RoleModel(1, "ADMIN"));

        when(jwtService.extractUsername(token)).thenReturn("regularuser");
        when(userRepository.findByUsername("regularuser")).thenReturn(Optional.of(UserConverter.toUserEntity(userModel2)));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                userService.deleteUser(userId, token));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("You do not have permission to delete this user", exception.getReason());
    }

    @Test
    void testDeleteUser_ShouldThrowNotFoundWhenUserDoesNotExist() {
        Integer userId = 1;
        String token = "admin_token";
        UserModel adminUser = new UserModel();
        adminUser.setId(1);
        adminUser.setRole(new RoleModel(1, "ADMIN"));

        when(jwtService.extractUsername(token)).thenReturn("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(UserConverter.toUserEntity(adminUser)));
        when(userRepository.existsById(userId)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                userService.deleteUser(userId, token));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("User not found", exception.getReason());
    }

    @Test
    void whenUserNotFound_thenNoSaveCalled() {
        when(userRepository.findById(42)).thenReturn(Optional.empty());

        userService.computeRupeeWallet(50, 42);

        verify(userRepository, never()).save(any());
    }

    @Test
    void whenAddingPositiveRupees_thenWalletIncreases() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.computeRupeeWallet(50, 1);

        assertEquals(150, user.getRupeeWallet());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void whenSubtractingLessThanBalance_thenWalletDecreases() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.computeRupeeWallet(-30, 1);

        assertEquals(70, user.getRupeeWallet());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void whenSubtractingMoreThanBalance_thenWalletFloorsToZero() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        userService.computeRupeeWallet(-200, 1);

        assertEquals(0, user.getRupeeWallet());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testCalculateWeeklyRupees_ShouldCalculateCorrectAmount() {
        int userId = 1;

        when(travelRepository.countCompletedTravelsByDriverIdAndDateBetween(anyInt(), any(), any()))
                .thenReturn(Optional.of(3L)); // 3 x 6 = 18
        when(travelRepository.countCompletedRecurringTravelsByDriverIdAndDateBetween(anyInt(), any(), any()))
                .thenReturn(Optional.of(2L)); // 2 x 8 = 16
        when(userTravelRepository.countConfirmedUserTravelsByUserIdAndDateBetween(anyInt(), any(), any()))
                .thenReturn(Optional.of(4L)); // 4 x 4 = 16

        int result = userService.calculateWeeklyRupees(userId);

        assertEquals(Math.round((18 + 16 + 16) * 3), result);
    }

    @Test
    void testGetWeeklyEnvironmentalStats_EmptyData_ShouldReturnZeros() {
        int userId = 1;

        when(travelRepository.countCompletedTravelsByDriverIdAndDateBetween(anyInt(), any(), any()))
                .thenReturn(Optional.of(0L));
        when(userTravelRepository.countUserTravelsForDriverByDateBetween(anyInt(), any(), any()))
                .thenReturn(Optional.of(0L));
        when(userTravelRepository.countConfirmedUserTravelsByUserIdAndDateBetween(anyInt(), any(), any()))
                .thenReturn(Optional.of(0L));
        when(travelRepository.countCompletedTravelsByDriverId(userId)).thenReturn(Optional.of(0L));
        when(userTravelRepository.countConfirmedUserTravelsByUserId(userId)).thenReturn(Optional.of(0L));
        when(userTravelRepository.countAllConfirmedPassengersForDriver(userId)).thenReturn(Optional.of(0L));

        WeeklyEnvironmentalStatsDTO dto = userService.getWeeklyEnvironmentalStats(userId);

        assertEquals(0, dto.getConfirmedRides());
        assertEquals(0, dto.getWeeklyRupees());
        assertEquals(0.0, dto.getAveragePassengersPerCompletedTrip());
        assertEquals(0.0, dto.getCo2SavedKg());
        assertEquals(0.0, dto.getCo2SavedKgTotal());
    }


    @Test
    void testCalculateTotalCo2Saved_ShouldReturnExpectedValue() {
        int userId = 1;

        when(travelRepository.countCompletedTravelsByDriverId(userId)).thenReturn(Optional.of(5L));
        when(userTravelRepository.countConfirmedUserTravelsByUserId(userId)).thenReturn(Optional.of(3L));
        when(userTravelRepository.countAllConfirmedPassengersForDriver(userId)).thenReturn(Optional.of(7L));

        WeeklyEnvironmentalStatsDTO dto = userService.getWeeklyEnvironmentalStats(userId);

        double expectedTotal = (5 + 3 + 7) * 10.0 * 0.21;
        assertEquals(expectedTotal, dto.getCo2SavedKgTotal());
    }

    @Test
    void testCalculateUserEcoScore_ShouldApplyWeightsCorrectly() {
        int userId = 1;

        when(travelRepository.countCompletedTravelsByDriverId(userId)).thenReturn(Optional.of(3L));     // 3 x 6 = 18
        when(travelRepository.countCompletedRecurringTravelsByDriverId(userId)).thenReturn(Optional.of(2L)); // 2 x 8 = 16
        when(userTravelRepository.countConfirmedUserTravelsByUserId(userId)).thenReturn(Optional.of(4L));    // 4 x 4 = 16

        when(userRepository.getUserIds()).thenReturn(List.of(userId));
        doNothing().when(userRepository).updateEcoRank(eq(userId), anyInt());

        userService.computeEcoRanks();

        verify(userRepository).updateEcoRank(userId, 2);
    }

    @Test
    void testDetermineEcoRankId_Boundaries() {
        assertEquals(1, invokeDetermineEcoRankId(0));
        assertEquals(2, invokeDetermineEcoRankId(50));
        assertEquals(3, invokeDetermineEcoRankId(150));
        assertEquals(4, invokeDetermineEcoRankId(300));
        assertEquals(5, invokeDetermineEcoRankId(600));
    }

    private int invokeDetermineEcoRankId(long score) {
        try {
            Method method = UserServiceImpl.class.getDeclaredMethod("determineEcoRankId", long.class);
            method.setAccessible(true);
            return (int) method.invoke(userService, score);
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
            return -1;
        }
    }
}
