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

    @Query(value = """
    SELECT *
    FROM TBL_STATE
    WHERE UPPER_CATE_NO = :cateNo
    ORDER BY STATE_NO
    """, nativeQuery = true)
    List<Map<String, Object>> loadScheduleStateByCate(@Param("cateNo") String cateNo);


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
    SELECT
        SC.SCH_NO,
        SC.SCH_TITLE,
        SC.SCH_ST_DATE,
        SC.SCH_ED_DATE,
        SC.SCH_CATEGORY,
        SC.SCH_STATE,
        SC.PAYMENT,
        SC.SCH_MEMO,
        CA.CATE_NO,
        CA.CATE_NAME,
        ST.STATE_NAME,
        ST.STATE_COLOR
    FROM TBL_SCHEDULE SC
        LEFT JOIN TBL_CATEGORY CA   ON SC.SCH_CATEGORY = CA.CATE_NO
        LEFT JOIN TBL_STATE ST      ON SC.SCH_STATE = ST.STATE_NO
    WHERE SCH_ST_DATE <= :schEndDate
        AND SCH_ED_DATE >= :schStrDate
    """, nativeQuery = true)
    List<Map<String, Object>> searchSchedule(
        @Param("schStrDate") String schStrDate,
        @Param("schEndDate") String schEndDate
    );

    @Modifying
    @Transactional
    @Query(value = """
    UPDATE TBL_SCHEDULE
    SET SCH_TITLE = :schTitle,
        SCH_ST_DATE = :schStDate,
        SCH_ED_DATE = :schEdDate,
        SCH_CATEGORY = :schCategory,
        SCH_STATE = :schState,
        PAYMENT = :payment,
        SCH_MEMO = :schMemo
    WHERE SCH_NO = :schNo
    """, nativeQuery = true)
    Integer updateSchedule(
            @Param("schNo") Long schNo,
            @Param("schTitle") String schTitle,
            @Param("schStDate") String schStDate,
            @Param("schEdDate") String schEdDate,
            @Param("schCategory") String schCategory,
            @Param("schState") String schState,
            @Param("payment") BigDecimal payment,
            @Param("schMemo") String schMemo
    );

    @Modifying
    @Transactional
    @Query(value = """
    DELETE FROM TBL_SCHEDULE
    WHERE SCH_NO = :schNo
    """, nativeQuery = true)
    void deleteSchedule(@Param("schNo") Long schNo);

}
