package com.andromedaastroshop.crudfullstack.crud_fullstack.user.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.user.repository.UserRepository;

import com.andromedaastroshop.crudfullstack.crud_fullstack.user.service.UserDetailServiceCustom;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailServiceCustom {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}