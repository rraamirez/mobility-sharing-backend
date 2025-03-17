package com.ramirezabril.mobility_sharing.actuator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * This component contributes detailed information about the database to the Spring Boot Actuator info endpoint.
 */
@Component
public class DatabaseInfoContributor implements InfoContributor {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Adds details about the database to the information exposed by the actuator.
     *
     * @param builder The Info.Builder object where the details are added.
     */
    @Override
    public void contribute(Info.Builder builder) {
        String version = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
        builder.withDetail("databaseVersion", version);

        String databaseName = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
        builder.withDetail("databaseName", databaseName);
    }
    //http://localhost:9090/actuator/info (see bd info)
}
