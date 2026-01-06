package it.polimi.mypolihub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import it.polimi.mypolihub.repository.UserRepository;

@Service
public class DbUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var u = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        var auth = new SimpleGrantedAuthority("ROLE_" + u.getRole().name());

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())   // username interno = email
                .password(u.getPassword())    // deve essere hash (bcrypt)
                .authorities(List.of(auth))
                .build();
    }
}
