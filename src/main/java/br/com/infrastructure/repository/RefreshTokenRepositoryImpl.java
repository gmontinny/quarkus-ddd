package br.com.infrastructure.repository;

import br.com.domain.model.RefreshToken;
import br.com.domain.repository.RefreshTokenRepository;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository, PanacheRepository<RefreshToken> {

    @Override
    public Uni<RefreshToken> findByToken(String token) {
        return find("token", token).firstResult();
    }

    @Override
    public Uni<RefreshToken> save(RefreshToken token) {
        return persist(token);
    }

    @Override
    public Uni<Integer> revokeAllByUsername(String username) {
        return update("revoked = true where username = ?1", username);
    }

    @Override
    public Uni<Long> deleteExpired() {
        return delete("expiresAt < ?1 or revoked = true", java.time.LocalDateTime.now());
    }
}
