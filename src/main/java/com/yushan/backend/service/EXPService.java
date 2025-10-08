package com.yushan.backend.service;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EXPService{

    @Autowired
    private UserMapper userMapper;

    // exp threshold
    private static final int[] THRESHOLDS = {100, 500, 2000, 5000};

    /**
     * add user exp with update sql
     * @param uuid
     * @param addExp
     */
    public void addExp(UUID uuid, Float addExp) {
        User user = userMapper.selectByPrimaryKey(uuid);
        user.setExp(user.getExp() + addExp);
        userMapper.updateByPrimaryKey(user);
    }

    /**
     * return the corresponding level based on exp
     * @param exp
     * @return corresponding level
     */
    public Integer checkLevel(Float exp) {
        if (exp == null) {
            return 1;
        }

        for (int i = 0; i < THRESHOLDS.length; i++) {
            if (exp < THRESHOLDS[i]) {
                return i + 1;
            }
        }
        return THRESHOLDS.length + 1;
        }
}
