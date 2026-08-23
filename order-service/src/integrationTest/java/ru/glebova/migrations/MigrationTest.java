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
    void shouldCreateOrdersTable() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'orders'",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldCreateOrderStatesTable() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'order_states'",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void shouldCreateTestDriveRequestsTable() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'test_drive_requests'",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }
}
