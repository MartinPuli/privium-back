package Marketplace.config.jwt;

import Marketplace.models.User;
import Marketplace.repositories.IUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.sql.SQLException;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        try {
            // Caso normal: si no existe el usuario, lanzamos UsernameNotFoundException
            User user = userRepository.findByEmail(email)
                    .orElseThrow();
            
            return new CustomUserDetails(user);
        } catch (SQLException e) {
            throw new BadCredentialsException(e.getMessage(), e);
        }
    }

}
