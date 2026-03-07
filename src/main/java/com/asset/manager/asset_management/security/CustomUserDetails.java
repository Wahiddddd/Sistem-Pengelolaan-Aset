package com.asset.manager.asset_management.security;

import com.asset.manager.asset_management.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Mengonversi Enum Role kita menjadi format yang dimengerti Spring Security
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
    }

    // Mengambil password dari database untuk divalidasi Spring Security
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Mengecek apakah kolom lockedUntil kosong atau sudah melewati waktu sekarang
        return user.getLockedUntil() == null || user.getLockedUntil().isBefore(java.time.LocalDateTime.now());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Akun hanya aktif jika belum dihapus (Soft Delete)
        return !user.isDeleted();
    }

    public User getUser() {
        return user;
    }
}
