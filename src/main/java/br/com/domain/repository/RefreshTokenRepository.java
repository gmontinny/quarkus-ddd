package br.com.domain.repository;

import br.com.domain.model.RefreshToken;
import io.smallrye.mutiny.Uni;
import java.util.Optional;

public interface RefreshTokenRepository {
    Uni<RefreshToken> findByToken(String token);
    Uni<RefreshToken> save(RefreshToken token);
    Uni<Integer> revokeAllByUsername(String username);
    Uni<Long> deleteExpired();
}
