package ru.glebova.migrations;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.glebova.IntegrationTestBase;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class MigrationTest extends IntegrationTestBase {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldApplyAllMigrations() {
        List<Map<String, Object>> changesets = jdbcTemplate.queryForList("SELECT * FROM databasechangelog");
        assertThat(changesets).isNotEmpty();
    }

    @Test
    void shouldCreateCarModelsTable() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'car_models'",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldCreateCarsTable() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'cars'",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldCreateTestableCarsTable() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'testable_cars'",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldCreateCarPartsTable() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'car_parts'",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldCreateSteeringWheelsTable() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'steering_wheels'",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldCreateWheelsTable() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'wheels'",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldCreateInteriorsTable() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'interiors'",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldCreateTransmissionsTable() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'transmissions'",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }
}
