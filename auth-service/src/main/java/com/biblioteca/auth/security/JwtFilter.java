package com.biblioteca.auth.security;

import com.biblioteca.auth.entity.Usuario;
import com.biblioteca.auth.repository.UsuarioRepository;
import com.biblioteca.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            // Validar que el token no esté vacío
            if (token != null && !token.trim().isEmpty()) {
                try {
                    String username = jwtService.extraerUsuario(token);

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        Usuario user = usuarioRepository.findByUsername(username).orElse(null);

                        if (user != null) {
                            UsernamePasswordAuthenticationToken auth =
                                    new UsernamePasswordAuthenticationToken(
                                            username,
                                            null,
                                            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRol()))
                                    );
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    }
                } catch (Exception e) {
                    // Si el token es inválido, simplemente continuar sin autenticar
                    // Los endpoints públicos seguirán funcionando
                    logger.error("Error al procesar el token JWT: " + e.getMessage());
                }
            }
        }
        chain.doFilter(request, response);
    }
}

