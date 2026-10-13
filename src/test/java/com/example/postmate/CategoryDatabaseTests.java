package com.example.postmate;

import com.example.postmate.repository.ScheduleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import javax.sql.DataSource;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryDatabaseTests {
    @Autowired JdbcTemplate jdbc;
    @Autowired DataSource dataSource;
    @Autowired ScheduleRepository schedules;

    @Test
    void migrationIsRepeatableAndPreservesHistoricalData() throws Exception {
        var categories = jdbc.queryForList("SELECT * FROM TBL_CATEGORY ORDER BY CATE_NO");
        var states = jdbc.queryForList("SELECT * FROM TBL_STATE ORDER BY STATE_NO");
        var scheduleRows = jdbc.queryForList("SELECT * FROM TBL_SCHEDULE ORDER BY SCH_NO");
        try (var connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("db/category-visibility.sql"));
        }
        assertEquals(categories, jdbc.queryForList("SELECT * FROM TBL_CATEGORY ORDER BY CATE_NO"));
        assertEquals(states, jdbc.queryForList("SELECT * FROM TBL_STATE ORDER BY STATE_NO"));
        assertEquals(scheduleRows, jdbc.queryForList("SELECT * FROM TBL_SCHEDULE ORDER BY SCH_NO"));
        assertEquals(3, jdbc.queryForObject("SELECT COUNT(*) FROM TBL_CATEGORY WHERE CATE_NAME IN ('체험단','플레이스 관리','히든마스터') AND IS_VISIBLE = FALSE", Integer.class));
        assertEquals(1, schedules.countVisibleCategory("8"));
        assertTrue(schedules.loadScheduleCategory().stream().noneMatch(row -> java.util.List.of("체험단", "플레이스 관리", "히든마스터").contains(row.get("CATE_NAME"))));
        assertEquals(java.util.List.of("업로드 전", "업로드 완료", "입금 완료"), schedules.loadScheduleStateByCate("8").stream().map(row -> row.get("STATE_NAME")).toList());
        assertEquals(jdbc.queryForObject("SELECT STATE_COLOR FROM TBL_STATE WHERE STATE_NO = 21", String.class),
                jdbc.queryForObject("SELECT STATE_COLOR FROM TBL_STATE WHERE STATE_NO = 80", String.class));
    }
}
