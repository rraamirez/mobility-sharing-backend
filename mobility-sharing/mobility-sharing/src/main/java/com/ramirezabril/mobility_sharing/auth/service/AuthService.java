package com.ramirezabril.mobility_sharing.auth.service;

import com.ramirezabril.mobility_sharing.auth.controller.LoginRequest;
import com.ramirezabril.mobility_sharing.auth.controller.RegisterRequest;
import com.ramirezabril.mobility_sharing.auth.controller.TokenResponse;
import com.ramirezabril.mobility_sharing.auth.repository.Token;
import com.ramirezabril.mobility_sharing.auth.repository.TokenRepository;
import com.ramirezabril.mobility_sharing.entity.Role;
import com.ramirezabril.mobility_sharing.entity.User;
import com.ramirezabril.mobility_sharing.repository.UserRepository;
import com.ramirezabril.mobility_sharing.util.EcoRanksUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final EcoRanksUtils ecoRanksUtils; //autowired with required args constructor
    private static final int DEFAULT_RANK = 1;


    public TokenResponse register(RegisterRequest registerRequest) {
        var user = User.builder()
                .name(registerRequest.name())
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .username(registerRequest.username())
                .rupeeWallet(100)
                .role(new Role(registerRequest.roleId(), null))
                .ecoRank(ecoRanksUtils.getEcoRankById(DEFAULT_RANK))
                .build();

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, refreshToken);
        return new TokenResponse(jwtToken, refreshToken);
    }

    public TokenResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username()
                        , loginRequest.password()
                )
        );
        var user = userRepository.findByUsername(loginRequest.username()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, refreshToken);
        return new TokenResponse(jwtToken, refreshToken);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(Token.TokenType.BEARER)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(token);
    }

    public TokenResponse refreshToken(String jwtToken) {
        if (jwtToken == null || !jwtToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid auth header");
        }
        final String refreshToken = jwtToken.substring(7);
        final String username = jwtService.extractUsername(refreshToken);
        if (username == null) {
            return null;
        }

        final User user = this.userRepository.findByUsername(username).orElseThrow();
        final boolean isTokenValid = jwtService.isTokenValid(refreshToken, user);
        if (!isTokenValid) {
            return null;
        }

        final String accessToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);

        return new TokenResponse(accessToken, refreshToken);
    }

    private void revokeAllUserTokens(final User user) {
        final List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (!validUserTokens.isEmpty()) {
            validUserTokens.forEach(token -> {
                token.setIsExpired(true);
                token.setIsRevoked(true);
            });
            tokenRepository.saveAll(validUserTokens);
        }
    }

}
