package com.reactive.example.messages.component.security.entity;

import com.reactive.example.messages.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class AdaptedUserDetails implements UserDetails {

    private final String login;
    private final String password;
    private List<GrantedAuthority> grantedAuthorities;

    public AdaptedUserDetails(User user) {
        this.login = user.getLogin();
        this.password = user.getPassword();
        this.grantedAuthorities = new LinkedList<>();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setGrantedAuthorities(List<GrantedAuthority> grantedAuthorities) {
        this.grantedAuthorities = grantedAuthorities;
    }

    public void addGrantedAuthorities(GrantedAuthority grantedAuthority) {
        this.grantedAuthorities.add(grantedAuthority);
    }
}
