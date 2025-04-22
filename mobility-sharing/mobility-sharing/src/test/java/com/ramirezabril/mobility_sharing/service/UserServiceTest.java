package com.ramirezabril.mobility_sharing.service;

import com.ramirezabril.mobility_sharing.auth.service.JwtService;
import com.ramirezabril.mobility_sharing.converter.UserConverter;
import com.ramirezabril.mobility_sharing.entity.Role;
import com.ramirezabril.mobility_sharing.entity.User;
import com.ramirezabril.mobility_sharing.model.RoleModel;
import com.ramirezabril.mobility_sharing.model.UserModel;
import com.ramirezabril.mobility_sharing.repository.UserRepository;
import com.ramirezabril.mobility_sharing.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

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
        when(userRepository.save(user)).thenReturn(user);

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
}
