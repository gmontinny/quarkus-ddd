package br.com.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record UserRequest(
        @NotBlank String username,
        @NotBlank @Email String email,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String password,
        @Pattern(regexp = "ROLE_USER|ROLE_MANAGER|ROLE_ADMIN", message = "Role must be ROLE_USER, ROLE_MANAGER or ROLE_ADMIN")
        String role
) {}
