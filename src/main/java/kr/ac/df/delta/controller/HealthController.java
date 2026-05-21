package kr.ac.df.delta.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/db")
    public ResponseEntity<Map<String, Object>> dbHealth() {
        Integer one = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        return ResponseEntity.ok(Map.of("status", "UP", "db", "postgres", "check", one));
    }
}
