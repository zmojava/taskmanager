package com.zeyt.springboot.taskmanager.util;


import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {
    // Секретный ключ, используемый для подписи JWT-токенов.
    // Этот ключ обеспечивает безопасность токена, поэтому его необходимо держать в секрете.
    @Value("${spring.jwt.secret}")
    private  String SECRET_KEY;

    // Время жизни токена (в миллисекундах). Здесь установлено 86400000 мс, что эквивалентно 1 дню.
    // После истечения этого времени токен становится недействительным.
    @Value("${spring.jwt.expiration}")
    private Long EXPIRATION_TIME;

    /**
     * Метод для генерации JWT токена.
     *
     * @param username имя пользователя, для которого создается токен.
     * @return строковое представление JWT токена.
     * <p>
     * Основные шаги:
     * 1. `setSubject` — указывает уникальный идентификатор токена, в данном случае — имя пользователя.
     * 2. `setIssuedAt` — указывает дату и время создания токена.
     * 3. `setExpiration` — указывает срок действия токена.
     * 4. `signWith` — подписывает токен с использованием алгоритма и секретного ключа.
     * 5. `compact` — генерирует токен в виде строки.
     */
    public String generateToken(String username) {
        return Jwts.builder() // Создает новый JWT-токен.
                .setSubject(username) // Устанавливает "subject" (основной объект токена). Обычно это имя пользователя.
                .setIssuedAt(new Date()) // Указывает время выпуска токена (текущее время).
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Устанавливает срок действия токена.
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // Подписывает токен с использованием алгоритма HS256 и секретного ключа.
                .compact(); // Собирает токен в строковый формат.
    }

    /**
     * Метод для извлечения имени пользователя из токена.
     *
     * @param token строковый JWT токен.
     * @return имя пользователя (subject) из токена.
     * <p>
     * Шаги:
     * 1. Парсит токен с использованием секретного ключа.
     * 2. Извлекает содержимое токена (claims).
     * 3. Возвращает значение "subject", которое содержит имя пользователя.
     */
    public String extractUsername(String token) {
        return Jwts.parser() // Создает парсер для разбора токена.
                .setSigningKey(SECRET_KEY) // Устанавливает секретный ключ для проверки подписи токена.
                .parseClaimsJws(token) // Проверяет подпись токена и извлекает его содержимое (claims).
                .getBody() // Получает claims — данные, закодированные внутри токена.
                .getSubject(); // Извлекает из claims значение "subject" (в данном случае, имя пользователя).
    }

    /**
     * Метод для проверки валидности токена.
     *
     * @param token строковый JWT токен.
     * @return true, если токен валиден, иначе false.
     * <p>
     * Метод проверяет:
     * 1. Корректность подписи токена.
     * 2. Срок действия токена (не истек ли он).
     * <p>
     * Если токен невалиден (например, подпись подделана или срок действия истек), возникает исключение, и метод возвращает false.
     */
    public boolean isTokenValid(String token) {
        try {
            // Парсер пытается разобрать токен и проверить его подпись.
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true; // Если исключений не возникло, токен валиден.
        } catch (JwtException | IllegalArgumentException e) {
            // JwtException — общее исключение для ошибок токена.
            // IllegalArgumentException — ошибка при передаче некорректного токена (например, null или пустая строка).
            return false; // Если произошла ошибка, токен считается невалидным.
        }
    }
}


