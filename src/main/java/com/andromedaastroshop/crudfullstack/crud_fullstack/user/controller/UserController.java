package com.andromedaastroshop.crudfullstack.crud_fullstack.user.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.UpdateUserRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.UserRespose;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.User;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserRespose> getMe(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.findById(currentUser.getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserRespose> updateMe(
            @AuthenticationPrincipal User currentUser,
            @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(userService.updateById(currentUser.getId(), request));
    }
}