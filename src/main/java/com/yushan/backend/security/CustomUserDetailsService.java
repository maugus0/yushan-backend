package com.yushan.backend.security;

import com.yushan.backend.dao.UserMapper;
import com.yushan.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Custom User Details Service
 * 
 * This service loads user details from database and converts User entity
 * to UserDetails object for Spring Security authentication
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    /**
     * Load user by username (email in this case)
     * 
     * @param username Username (email) to load
     * @return UserDetails object
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // In this application, username is actually email
        User user = userMapper.selectByEmail(username);
        
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        
        return createUserDetails(user);
    }

    /**
     * Load user by email
     * 
     * @param email Email to load
     * @return UserDetails object
     * @throws UsernameNotFoundException if user not found
     */
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        User user = userMapper.selectByEmail(email);
        
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        
        return createUserDetails(user);
    }

    /**
     * Create UserDetails object from User entity
     * 
     * @param user User entity
     * @return UserDetails object
     */
    private UserDetails createUserDetails(User user) {
        return new CustomUserDetails(user);
    }

    /**
     * Custom UserDetails implementation
     * 
     * This class wraps User entity and implements UserDetails interface
     * for Spring Security integration
     */
    public static class CustomUserDetails implements UserDetails {
        private final User user;

        public CustomUserDetails(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            List<GrantedAuthority> authorities = new ArrayList<>();
            
            // Add basic user authority
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            
            // Add author authority if user is an author
            if (user.getIsAuthor() != null && user.getIsAuthor()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_AUTHOR"));
            }
            
            // Add admin authority if user is admin (you can add this logic later)
            // if (user.getRole() != null && user.getRole().equals("ADMIN")) {
            //     authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            // }
            
            return authorities;
        }

        @Override
        public String getPassword() {
            return user.getHashPassword();
        }

        @Override
        public String getUsername() {
            return user.getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true; // Account never expires
        }

        @Override
        public boolean isAccountNonLocked() {
            return true; // Account never locked
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true; // Credentials never expire
        }

        @Override
        public boolean isEnabled() {
            // Check if user status is active (1 = active, 0 = inactive)
            return user.getStatus() != null && user.getStatus() == 1;
        }

        /**
         * Get the underlying User entity
         * 
         * @return User entity
         */
        public User getUser() {
            return user;
        }

        /**
         * Get user UUID
         * 
         * @return User UUID
         */
        public String getUserId() {
            return user.getUuid().toString();
        }

        /**
         * Check if user is author
         * 
         * @return true if user is author, false otherwise
         */
        public boolean isAuthor() {
            return user.getIsAuthor() != null && user.getIsAuthor();
        }

        /**
         * Check if user is verified author
         * 
         * @return true if user is verified author, false otherwise
         */
        public boolean isVerifiedAuthor() {
            return user.getAuthorVerified() != null && user.getAuthorVerified();
        }
    }
}
