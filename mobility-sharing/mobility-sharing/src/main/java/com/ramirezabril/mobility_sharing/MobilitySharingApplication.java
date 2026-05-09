package com.ramirezabril.mobility_sharing;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MobilitySharingApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        setPropertyFromDotenv("DB_URL", dotenv);
        setPropertyFromDotenv("DB_USERNAME", dotenv);
        setPropertyFromDotenv("DB_PASSWORD", dotenv);
        setPropertyFromDotenv("DB_DRIVER_CLASS", dotenv);

        setPropertyFromDotenv("JWT_SECRET_KEY", dotenv);
        setPropertyFromDotenv("JWT_EXPIRATION", dotenv);
        setPropertyFromDotenv("JWT_REFRESH_TOKEN_EXPIRATION", dotenv);

        setPropertyFromDotenv("SPRING_SECURITY_USER_NAME", dotenv);
        setPropertyFromDotenv("SPRING_SECURITY_USER_PASSWORD", dotenv);

        setPropertyFromDotenv("SWAGGER_UI_PATH", dotenv);

        SpringApplication.run(MobilitySharingApplication.class, args);
    }

    private static void setPropertyFromDotenv(String key, Dotenv dotenv) {
        String value = dotenv.get(key);
        if (value != null && !value.isBlank()) {
            System.setProperty(key, value);
        }
    }
}
