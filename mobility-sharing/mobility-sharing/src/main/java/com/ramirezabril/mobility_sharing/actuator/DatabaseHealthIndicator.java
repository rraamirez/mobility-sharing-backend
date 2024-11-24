package com.ramirezabril.mobility_sharing.actuator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Health indicator for the database, to be used in readiness and liveness endpoints.
 */
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Verifies if the database is accessible and returns health status accordingly.
     *
     * @return Health status of the database.
     */
    @Override
    public Health health() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return Health.up().withDetail("database", "connected").build();
        } catch (Exception e) {
            return Health.down(e).withDetail("database", "not connected").build();
        }
    }
    //http://localhost:8080/actuator/health/readiness
    //http://localhost:8080/actuator/health/liveness
}
