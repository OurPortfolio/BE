package com.sparta.ourportfolio.common.security;

import com.sparta.ourportfolio.JacocoGenerated;
import com.sparta.ourportfolio.common.enums.UserRoleEnum;
import com.sparta.ourportfolio.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public record UserDetailsImpl(User user) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRoleEnum role = UserRoleEnum.USER;
        String authority = role.getAuthority();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    @JacocoGenerated
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JacocoGenerated
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JacocoGenerated
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JacocoGenerated
    @Override
    public boolean isEnabled() {
        return true;
    }
}
