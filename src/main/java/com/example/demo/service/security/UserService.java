package com.example.demo.service.security;

import com.example.demo.dao.UserRepository;
import com.example.demo.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> allUsers() {
        return new ArrayList<>(userRepository.findAll());
    }
}
