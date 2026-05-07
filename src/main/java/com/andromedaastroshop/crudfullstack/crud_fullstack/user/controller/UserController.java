package com.andromedaastroshop.crudfullstack.crud_fullstack.user.controller;

import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.UpdateUserRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.dto.UserRespose;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.model.Role;
import com.andromedaastroshop.crudfullstack.crud_fullstack.user.service.UserService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UserRespose> getUser(@PathVariable Long id){
        return ResponseEntity.ok(userService.findById(id));
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<UserRespose> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    @GetMapping("/{id}/role/{role}")
    public ResponseEntity<UserRespose> getUserByIdAndRole(@PathVariable Long id, @PathVariable Role role) {
        return ResponseEntity.ok(userService.findByIdAndRole(id, role));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserRespose>> getAllUsersByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.findAllByRole(role));
    }

    @GetMapping
    public ResponseEntity<List<UserRespose>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserRespose>> search(@RequestParam String q) {
        return ResponseEntity.ok(userService.search(q));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserRespose> updateUser(@PathVariable Long id,@RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateById(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
