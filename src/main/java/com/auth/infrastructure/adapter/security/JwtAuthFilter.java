package com.auth.infrastructure.adapter.security;

import com.auth.application.port.TokenService;
import com.auth.infrastructure.adapter.persistence.SpringDataUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filtro JWT — intercepta cada request y valida el token.
 *
 * Flujo:
 *  1. Lee el header Authorization: Bearer <token>
 *  2. Extrae y valida el JWT
 *  3. Carga el usuario desde la BD
 *  4. Registra la autenticación en el SecurityContext
 *  5. Deja pasar el request al Controller
 *
 * Si el token es inválido o no existe → Spring Security devuelve 401.
 * OncePerRequestFilter garantiza que el filtro se ejecute una sola vez por request.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final TokenService             tokenService;
    private final SpringDataUserRepository userRepository;

    public JwtAuthFilter(TokenService tokenService,
                         SpringDataUserRepository userRepository) {
        this.tokenService   = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Leer el header Authorization
        String authHeader = request.getHeader("Authorization");

        // Si no hay header o no empieza con "Bearer ", dejamos pasar
        // Spring Security se encargará de rechazarlo si el endpoint lo requiere
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraer el token — quitamos "Bearer " (7 caracteres)
        String token = authHeader.substring(7);

        // 3. Validar el token
        if (!tokenService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4. Extraer el email del token
        String email = tokenService.extractEmail(token);

        // 5. Solo autenticamos si no hay autenticación previa en el contexto
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Cargar el usuario desde la BD para obtener sus roles
            userRepository.findByEmail(email).ifPresent(userEntity -> {

                // 7. Convertir roles a authorities de Spring Security
                List<SimpleGrantedAuthority> authorities = userEntity.getRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList());

                // 8. Crear el token de autenticación de Spring Security
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                // 9. Registrar la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            });
        }

        // 10. Continuar con el siguiente filtro
        filterChain.doFilter(request, response);
    }
}