package ntnu.idatt2106.backend.service;


import lombok.RequiredArgsConstructor;
import ntnu.idatt2106.backend.model.User;
import ntnu.idatt2106.backend.model.authentication.AuthenticationRequest;
import ntnu.idatt2106.backend.model.authentication.RegisterRequest;
import ntnu.idatt2106.backend.model.dto.response.AuthenticationResponse;
import ntnu.idatt2106.backend.model.enums.Role;
import ntnu.idatt2106.backend.repository.UserRepository;
import org.apache.http.auth.InvalidCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ntnu.idatt2106.backend.exceptions.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    Logger logger = LoggerFactory.getLogger(AuthenticationService.class);



    /**

     Registers a new user and returns an authentication token.

     @param request a RegisterRequest object containing the user's email, name, and password

     @return an AuthenticationResponse object containing an authentication token

     @throws UserAlreadyExistsException if the email is already in use
     */
    public AuthenticationResponse register(RegisterRequest request) throws UserAlreadyExistsException {
        var user = User.builder()
                .id(String.valueOf(java.util.UUID.randomUUID()))
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        if (repository.findByEmail(user.getEmail()).isPresent())
            throw new UserAlreadyExistsException("Email is already in use");
        repository.save(user);
        var jwToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwToken).build();
    }


    /**

     Authenticates a user and returns an authentication token.
     @param request an AuthenticationRequest object containing the user's email and password
     @return an AuthenticationResponse object containing an authentication token
     @throws InvalidCredentialsException if the user credentials are invalid
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) throws InvalidCredentialsException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail() , request.getPassword()));
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        var user = repository.findByEmail(request.getEmail()).orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
        var jwToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwToken).build();
    }


}

