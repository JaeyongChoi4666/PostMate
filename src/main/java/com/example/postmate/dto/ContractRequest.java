package com.example.postmate.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ContractRequest {

    private String contractTitle;

    private LocalDate strDate;

    private LocalDate endDate;

    private String contractCategory;

    private BigDecimal contractPayment; // 금액은 BigDecimal 권장

    private String contractChannel;

}
