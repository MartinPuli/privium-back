package Marketplace.config.jwt;

import Marketplace.models.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;


public class CustomUserDetails extends org.springframework.security.core.userdetails.User {

    private final User user;

    public CustomUserDetails(User user) {
        super(user.getEmail(), user.getPasswordHash(),
               List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
