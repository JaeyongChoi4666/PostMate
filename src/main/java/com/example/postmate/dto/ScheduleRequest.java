package com.example.postmate.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ScheduleRequest {

    private String scheduleTitle;

    private LocalDate schStrDate;

    private LocalDate schEndDate;

    private String scheduleCategory;

    private BigDecimal schedulePayment; // 금액은 BigDecimal 권장

    private String scheduleMemo;

}
