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
public interface ContractRepository extends JpaRepository<Contract, Long> {

    @Modifying
    @Transactional
    @Query(value = """
    INSERT INTO
        TBL_CONTRACT (CON_TITLE, CON_ST_DATE, CON_ED_DATE, CON_CATEGORY, CON_STATE, PAYMENT, CON_CHANNEL)
    VALUES (:conTitle,:conStDate,:conEdDate,:conCategory,'계약중',:payment,:conChannel)
    """, nativeQuery = true)
    Integer addContract(
            @Param("conTitle") String conTitle,
            @Param("conStDate") String conStDate,
            @Param("conEdDate") String conEdDate,
            @Param("conCategory") String conCategory,
            @Param("payment") BigDecimal payment,
            @Param("conChannel") String conChannel
    );

    @Query(value = """
    SELECT *
    FROM TBL_CONTRACT
    WHERE
        (:conTitle IS NULL OR :conTitle = '' OR CON_TITLE LIKE CONCAT('%', :conTitle, '%'))
    AND (CON_ST_DATE < NOW() AND CON_ED_DATE > NOW())
    """, nativeQuery = true)
    List<Map<String, Object>> searchContract(String conTitle);

    @Query(value = """
    SELECT *
    FROM TBL_CONTRACT
    WHERE
        (:conTitle IS NULL OR :conTitle ='' OR CON_TITLE LIKE CONCAT('%', :conTitle, '%'))
    """, nativeQuery = true)
    List<Map<String, Object>> searchContractAll(String conTitle);

}
