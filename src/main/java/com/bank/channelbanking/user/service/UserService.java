package com.bank.channelbanking.user.service;

import com.bank.channelbanking.user.dto.request.SignUpRequest;
import com.bank.channelbanking.user.dto.response.SignUpResponse;
import com.bank.channelbanking.user.entity.User;
import com.bank.channelbanking.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        String email = signUpRequest.email();
        String password = passwordEncoder.encode(signUpRequest.password());
        String name = signUpRequest.name();

        if (userRepository.existsByEmail(email)){
            throw new RuntimeException("이미 존재하는 회원입니다.");
        }

        User user = User.builder()
                .email(email)
                .password(password)
                .name(name)
                .build();
        userRepository.save(user);

        return SignUpResponse.builder()
                .email(email)
                .name(name)
                .build();
    }


}
