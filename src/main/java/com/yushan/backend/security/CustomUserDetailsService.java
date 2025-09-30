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
        private static final long serialVersionUID = 1L;

        private final String userId;
        private final String email;
        private final String displayUsername;
        private final String hashPassword;
        private final Boolean isAuthor;
        private final Boolean authorVerified;
        private final Integer status;

        public CustomUserDetails(User user) {
            this.userId = user.getUuid() != null ? user.getUuid().toString() : null;
            this.email = user.getEmail();
            this.displayUsername = user.getUsername();
            this.hashPassword = user.getHashPassword();
            this.isAuthor = user.getIsAuthor();
            this.authorVerified = user.getAuthorVerified();
            this.status = user.getStatus();
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            List<GrantedAuthority> authorities = new ArrayList<>();
            
            // Add basic user authority
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            
            // Add author authority if user is an author
            if (isAuthor != null && isAuthor) {
                authorities.add(new SimpleGrantedAuthority("ROLE_AUTHOR"));
            }

            // Add admin authority if user is admin (you can add this logic later)
            if (email != null && "admin@example.com".equals(email)) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            
            return authorities;
        }

        @Override
        public String getPassword() {
            return hashPassword;
        }

        @Override
        public String getUsername() {
            return email;
        }

        /**
         * Display username (profile name), not used for authentication
         */
        public String getProfileUsername() {
            return displayUsername;
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
            return status != null && status == 1;
        }

        /**
         * Get user UUID
         * 
         * @return User UUID
         */
        public String getUserId() {
            return userId;
        }

        /**
         * Check if user is author
         * 
         * @return true if user is author, false otherwise
         */
        public boolean isAuthor() {
            return isAuthor != null && isAuthor;
        }

        /**
         * Check if user is verified author
         * 
         * @return true if user is verified author, false otherwise
         */
        public boolean isVerifiedAuthor() {
            return authorVerified != null && authorVerified;
        }
    }
}
