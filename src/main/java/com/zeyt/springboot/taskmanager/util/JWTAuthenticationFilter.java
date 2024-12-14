package com.zeyt.springboot.taskmanager.util;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


// Класс фильтра для аутентификации через JWT, наследуется от OncePerRequestFilter,
// что означает, что фильтр будет применяться только один раз на запрос.
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    // Сервис для работы с JWT токенами, используется для извлечения информации из токена
    private final JWTUtil jwtUtil;
    // Сервис для загрузки данных о пользователе, используется для поиска пользователя по имени
    private final UserDetailsService userDetailsService;

    // Конструктор фильтра, принимающий зависимости для работы с JWT и загрузки данных пользователя.
    public JWTAuthenticationFilter(JWTUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil; // Инициализируем поле jwtUtil
        this.userDetailsService = userDetailsService; // Инициализируем поле userDetailsService
    }

    // Переопределяем метод doFilterInternal, который выполняется при каждом запросе.
    // Этот метод выполняет логику аутентификации пользователя через JWT токен.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Извлекаем заголовок Authorization из запроса, который может содержать JWT токен.
        final String authorizationHeader = request.getHeader("Authorization");

        // Переменные для хранения имени пользователя и самого токена
        String username = null;
        String token = null;

        // Проверяем, если заголовок Authorization присутствует и начинается с "Bearer ".
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7); // Убираем "Bearer " из начала строки (оставляем только токен).
            username = jwtUtil.extractUsername(token); // Извлекаем имя пользователя из токена с помощью метода jwtUtil.
        }

        // Проверяем, если имя пользователя не null и если аутентификация в SecurityContext ещё не выполнена
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Загружаем данные пользователя по имени, если токен предоставлен и пользователь найден
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Проверяем, если токен действителен с помощью метода jwtUtil.
            if (jwtUtil.isTokenValid(token)) {
                // Создаём объект аутентификации с информацией о пользователе и его правах.
                var authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                // Устанавливаем дополнительные детали аутентификации, такие как источник запроса.
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Сохраняем информацию о текущей аутентификации в SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Переходим к следующему фильтру в цепочке обработки запроса.
        chain.doFilter(request, response);
    }
}
