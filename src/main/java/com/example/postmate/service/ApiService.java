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
        validateSchedule(request, false);
        String schState = request.getScheduleState();
        if (schState == null || schState.equals("0")) {
            schState = scheduleRepository.initialState(request.getScheduleCategory());
        }
        if (schState == null) {
            throw new IllegalArgumentException("선택한 일정 종류에 상태가 없습니다.");
        }
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
        if (request.getSchStrDate() != null && request.getSchEndDate() != null
                && request.getSchEndDate().isBefore(request.getSchStrDate())) {
            request.setSchEndDate(request.getSchStrDate());
        }
        validateSchedule(request, true);
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

    private void validateSchedule(ScheduleRequest request, boolean editing) {
        if (request.getSchStrDate() == null || request.getSchEndDate() == null) {
            throw new IllegalArgumentException("일정 시작일과 종료일을 입력해주세요.");
        }
        if (request.getSchEndDate().isBefore(request.getSchStrDate())) {
            throw new IllegalArgumentException("종료일은 시작일과 같거나 이후여야 합니다.");
        }
        if (request.getScheduleTitle() == null || request.getScheduleTitle().isBlank()
                || request.getScheduleTitle().length() > 45) {
            throw new IllegalArgumentException("일정명은 1~45자로 입력해주세요.");
        }
        if (request.getSchedulePayment() == null || request.getSchedulePayment().signum() < 0
                || request.getSchedulePayment().compareTo(java.math.BigDecimal.valueOf(Integer.MAX_VALUE)) > 0) {
            throw new IllegalArgumentException("수익금은 0~2,147,483,647원 사이로 입력해주세요.");
        }
        if (request.getSchedulePayment().remainder(java.math.BigDecimal.valueOf(100)).signum() != 0) {
            throw new IllegalArgumentException("수익금은 100원 단위로 입력해주세요.");
        }
        if (request.getScheduleMemo() != null && request.getScheduleMemo().length() > 45) {
            throw new IllegalArgumentException("일정 상세는 45자 이내로 입력해주세요.");
        }
        if (request.getScheduleCategory() == null || !request.getScheduleCategory().matches("[1-9][0-9]*")) {
            throw new IllegalArgumentException("일정 종류를 선택해주세요.");
        }
        if (editing && (request.getScheduleNo() == null || request.getScheduleState() == null
                || request.getScheduleState().equals("0"))) {
            throw new IllegalArgumentException("수정할 일정과 일정 상태를 선택해주세요.");
        }
        if (scheduleRepository.countVisibleCategory(request.getScheduleCategory()) == 0
                && !(editing && scheduleRepository.countExistingCategory(request.getScheduleNo(), request.getScheduleCategory()) > 0)) {
            throw new IllegalArgumentException("표시되는 일정 종류를 선택해주세요.");
        }
        if (request.getScheduleState() != null && !request.getScheduleState().equals("0")
                && scheduleRepository.countCategoryState(request.getScheduleCategory(), request.getScheduleState()) == 0) {
            throw new IllegalArgumentException("일정 종류에 맞는 상태를 선택해주세요.");
        }
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
