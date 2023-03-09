package com.example.rest_api_jwt.services;

import com.example.rest_api_jwt.Controllers.auth.AuthenticateRequest;
import com.example.rest_api_jwt.Controllers.auth.AuthenticationResponse;
import com.example.rest_api_jwt.Controllers.auth.RegisterRequest;
import com.example.rest_api_jwt.config.JWTService;
import com.example.rest_api_jwt.entities.Role;
import com.example.rest_api_jwt.entities.User;
import com.example.rest_api_jwt.repositories.UserRepository;
import com.example.rest_api_jwt.utils.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> register(RegisterRequest request) {
        if(request.getFirstName()==null || request.getFirstName().isEmpty())
            return new ResponseEntity(new ApiResponse(false, "First name is required!", 400, null),
                    HttpStatus.BAD_REQUEST);
        if(request.getLastName()==null || request.getLastName().isEmpty())
            return new ResponseEntity(new ApiResponse(false, "Last name is required!", 400, null),
                    HttpStatus.BAD_REQUEST);
        if(request.getEmail()==null || request.getEmail().isEmpty())
            return new ResponseEntity(new ApiResponse(false, "Email is required!", 400, null),
                    HttpStatus.BAD_REQUEST);
        if(request.getPassword()==null || request.getPassword().isEmpty())
            return new ResponseEntity(new ApiResponse(false, "Password is required!", 400, null),
                    HttpStatus.BAD_REQUEST);
        if(userRepository.existsByEmail(request.getEmail()))
            return new ResponseEntity(new ApiResponse(false, "Email is already taken!", 400, null),
                    HttpStatus.BAD_REQUEST);

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);

        return  ResponseEntity.ok(new AuthenticationResponse(jwtToken, user));
    }

    public ResponseEntity<?> authenticate(AuthenticateRequest request) {

        if (request.getEmail() == null || request.getEmail().isEmpty())
            return new ResponseEntity(new ApiResponse(false, "Email is required!", 400, null),
                    HttpStatus.BAD_REQUEST);

        if (request.getPassword() == null || request.getPassword().isEmpty())
            return new ResponseEntity(new ApiResponse(false, "Password is required!", 400, null),
                    HttpStatus.BAD_REQUEST);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
            var jwtToken = jwtService.generateToken(user);
            return  ResponseEntity.ok(new AuthenticationResponse(jwtToken, user));

        } catch (Exception e) {
            return new ResponseEntity(new ApiResponse(false, "Invalid email or password", 401, null),
                    HttpStatus.UNAUTHORIZED);
        }

        
    }
}
