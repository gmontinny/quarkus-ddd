package br.com.infrastructure.ratelimit;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Provider
@Priority(Priorities.USER - 100)
public class RateLimitFilter implements ContainerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private final Map<String, RateLimitEntry> requestCounts = new ConcurrentHashMap<>();

    @Override
    public void filter(ContainerRequestContext ctx) {
        String clientIp = ctx.getHeaderString("X-Forwarded-For");
        if (clientIp == null) clientIp = "unknown";

        RateLimitEntry entry = requestCounts.computeIfAbsent(clientIp, k -> new RateLimitEntry());

        if (!entry.tryAcquire()) {
            ctx.abortWith(Response.status(429)
                    .header("X-RateLimit-Limit", MAX_REQUESTS_PER_MINUTE)
                    .header("X-RateLimit-Remaining", 0)
                    .header("Retry-After", "60")
                    .entity(Map.of("error", "Too Many Requests", "message", "Rate limit exceeded. Try again in 60 seconds."))
                    .build());
        } else {
            ctx.getHeaders().add("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS_PER_MINUTE));
            ctx.getHeaders().add("X-RateLimit-Remaining", String.valueOf(entry.remaining()));
        }
    }

    private static class RateLimitEntry {
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile long windowStart = System.currentTimeMillis();

        boolean tryAcquire() {
            long now = System.currentTimeMillis();
            if (now - windowStart > 60_000) {
                count.set(0);
                windowStart = now;
            }
            return count.incrementAndGet() <= MAX_REQUESTS_PER_MINUTE;
        }

        int remaining() {
            return Math.max(0, MAX_REQUESTS_PER_MINUTE - count.get());
        }
    }
}
