package com.andromedaastroshop.crudfullstack.crud_fullstack.user.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.UserRespose;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.Role;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    public UserController(UserService userService) {
        this.userService = userService;
    }

    private final UserService userService;

    @GetMapping("/{id}")
    public UserRespose getUser(@PathVariable Long id){
        return userService.findById(id);
    }
    
    @GetMapping("/email/{email}")
    public UserRespose getUserByEmail(@PathVariable String email) {
        return userService.findByEmail(email);
    }

    @GetMapping("/{id}/role/{role}")
    public UserRespose getUserByIdAndRole(@PathVariable Long id, @PathVariable Role role) {
        return userService.findByIdAndRole(id, role);
    }

    @GetMapping("/role/{role}")
    public List<UserRespose> getAllUsersByRole(@PathVariable Role role) {
        return userService.findAllByRole(role);
    }

    @GetMapping
    public  List<UserRespose> getAllUsers() {
        return userService.findAllUsers();
    }
}
