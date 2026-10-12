package br.com.domain.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends PanacheEntity {

    @Column(name = "token", nullable = false, unique = true)
    public String token;

    @Column(name = "username", nullable = false)
    public String username;

    @Column(name = "expires_at", nullable = false)
    public LocalDateTime expiresAt;

    @Column(name = "revoked")
    public boolean revoked = false;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !revoked && !isExpired();
    }
}
