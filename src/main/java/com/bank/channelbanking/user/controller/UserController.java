package com.bank.channelbanking.user.controller;

import com.bank.channelbanking.global.annotation.Idempotent;
import com.bank.channelbanking.user.dto.request.SignUpRequest;
import com.bank.channelbanking.user.dto.response.SignUpResponse;
import com.bank.channelbanking.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "User API", description = "User Controller")
public class UserController {

    private final UserService userService;

    //@Idempotent
    @PostMapping("/signUp")
    @Operation(summary = "sign up", description = "새로운 고객 생성")
    public SignUpResponse signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        return userService.signUp(signUpRequest);
    }
}
