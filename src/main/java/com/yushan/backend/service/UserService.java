package com.yushan.backend.service;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserRepository {
    @Autowired
    private UserMapper userMapper;

}

