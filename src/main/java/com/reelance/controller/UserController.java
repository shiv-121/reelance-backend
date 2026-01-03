package com.reelance.controller;

import com.reelance.dto.MeResponse;
import com.reelance.entity.User;
import com.reelance.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> getCurrentUser() {

        User user = userService.getCurrentUser();

        return ResponseEntity.ok(
                new MeResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole()
                )
        );
    }
}
