package com.example.postmate.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ScheduleRequest {
    private Long scheduleNo;
    private String scheduleTitle;
    private LocalDate schStrDate;
    private LocalDate schEndDate;
    private String scheduleCategory;
    private String scheduleState;
    private BigDecimal schedulePayment;
    private Integer preTax;
    private String scheduleMemo;

}
