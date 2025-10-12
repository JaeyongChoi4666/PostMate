package com.example.postmate.repository;

import com.example.postmate.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public interface ScheduleRepository extends JpaRepository<Contract, Long> {

    @Query(value = """
    SELECT *
    FROM TBL_CATEGORY
    ORDER BY CATE_NO
    """, nativeQuery = true)
    List<Map<String, Object>> loadScheduleCategory();

    @Modifying
    @Transactional
    @Query(value = """
    INSERT INTO
        TBL_SCHEDULE (SCH_TITLE, SCH_ST_DATE, SCH_ED_DATE, SCH_CATEGORY, SCH_STATE, PAYMENT, SCH_MEMO)
    VALUES (:schTitle,:schStDate,:schEdDate,:schCategory,:schState,:payment,:schMemo)
    """, nativeQuery = true)
    Integer addSchedule(
        @Param("schTitle") String schTitle,
        @Param("schStDate") String schStDate,
        @Param("schEdDate") String schEdDate,
        @Param("schCategory") String schCategory,
        @Param("schState") String schState,
        @Param("payment") BigDecimal payment,
        @Param("schMemo") String schMemo
    );

    @Query(value = """
    SELECT *
    FROM TBL_SCHEDULE
    WHERE SCH_ST_DATE <= :schEndDate
        AND SCH_ED_DATE >= :schStrDate
    """, nativeQuery = true)
    List<Map<String, Object>> searchSchedule(
        @Param("schStrDate") String schStrDate,
        @Param("schEndDate") String schEndDate
    );
}
