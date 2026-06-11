package com.chantier.sgvac.api;

import com.chantier.sgvac.api.dto.LoginRequest;
import com.chantier.sgvac.api.dto.LoginResponse;
import com.chantier.sgvac.api.dto.UserResponse;
import com.chantier.sgvac.auth.ApiAuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthApiController {

    private final ApiAuthService apiAuthService;

    public AuthApiController(ApiAuthService apiAuthService) {
        this.apiAuthService = apiAuthService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return apiAuthService.login(request);
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst()
                .orElse("AGENT");
        return new UserResponse(authentication.getName(), role);
    }
}
