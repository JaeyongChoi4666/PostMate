package com.example.postmate.service;

import com.example.postmate.dto.ContractRequest;
import com.example.postmate.dto.ScheduleRequest;
import com.example.postmate.repository.ContractRepository;
import com.example.postmate.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApiService {

    private final ContractRepository contractRepository;
    private final ScheduleRepository scheduleRepository;

    //계약 관련 service
    public int addContract(ContractRequest request) {
        return contractRepository.addContract(
                request.getContractTitle(),
                request.getStrDate().toString(),
                request.getEndDate().toString(),
                request.getContractCategory(),
                request.getContractPayment(),
                request.getContractChannel()
                );
    }

    public List<Map<String, Object>> searchContract(String conTitle){
        return contractRepository.searchContract(conTitle);
    }

    public List<Map<String, Object>> searchContractAll(String conTitle){
        return contractRepository.searchContractAll(conTitle);
    }

    public void deleteContract(Long conId) {
        contractRepository.deleteById(conId);
    }

    //일정 관련 service
    public List<Map<String, Object>> loadScheduleCategory(){
        return scheduleRepository.loadScheduleCategory();
    }

    public List<Map<String, Object>> loadScheduleStateByCate(String cateNo){
        return scheduleRepository.loadScheduleStateByCate(cateNo);
    }

    public int addSchedule(ScheduleRequest request) {
        String schState = switch (request.getScheduleCategory()) {
            case "1" -> "10";
            case "2" -> "20";
            case "3" -> "30";
            case "4" -> "40";
            case "5" -> "50";
            case "6" -> "60";
            case "7" -> "70";
            default -> null;
        };
        return scheduleRepository.addSchedule(
                request.getScheduleTitle(),
                request.getSchStrDate().toString(),
                request.getSchEndDate().toString(),
                request.getScheduleCategory(),
                schState,
                request.getSchedulePayment(),
                request.getPreTax(),
                request.getScheduleMemo()
        );
    }

    public List<Map<String, Object>> searchSchedule(String schStrDate,String schEndDate){
        return scheduleRepository.searchSchedule(schStrDate,schEndDate);
    }

    public int updateSchedule(ScheduleRequest request) {
        return scheduleRepository.updateSchedule(
                request.getScheduleNo(),
                request.getScheduleTitle(),
                request.getSchStrDate().toString(),
                request.getSchEndDate().toString(),
                request.getScheduleCategory(),
                request.getScheduleState(),
                request.getSchedulePayment(),
                request.getPreTax(),
                request.getScheduleMemo()
        );
    }

    public void deleteSchedule(Long schNo) {
        scheduleRepository.deleteSchedule(schNo);
    }

    public Map<String, Object> getMonthlyPaymentSum(int year, int month) {
        // 해당 월의 첫날과 마지막 날 계산
        String firstDay = String.format("%d-%02d-01", year, month);

        // 마지막 날 계산
        int lastDayOfMonth;
        if (month == 12) {
            lastDayOfMonth = 31;
        } else {
            // 다음 달 1일에서 하루 뺀 날짜의 일자
            java.time.LocalDate nextMonth = java.time.LocalDate.of(year, month, 1).plusMonths(1);
            lastDayOfMonth = nextMonth.minusDays(1).getDayOfMonth();
        }
        String lastDay = String.format("%d-%02d-%02d", year, month, lastDayOfMonth);

        // 일정 수익금 합계
        Map<String, Object> scheduleSum = scheduleRepository.getMonthlyPaymentSum(year, month, firstDay, lastDay);
        long schedulePayment = ((Number) scheduleSum.get("TOTAL_PAYMENT")).longValue();

        // 계약 수익금 합계 (계약 시작일이 해당 월인 것만)
        Map<String, Object> contractSum = contractRepository.getMonthlyContractPaymentSum(year, month);
        long contractPayment = ((Number) contractSum.get("TOTAL_PAYMENT")).longValue();

        // 합산
        long totalPayment = schedulePayment + contractPayment;

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("TOTAL_PAYMENT", totalPayment);
        result.put("SCHEDULE_PAYMENT", schedulePayment);
        result.put("CONTRACT_PAYMENT", contractPayment);

        return result;
    }
}
