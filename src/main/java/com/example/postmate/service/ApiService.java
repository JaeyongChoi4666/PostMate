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
            default -> null;
        };
        return scheduleRepository.addSchedule(
                request.getScheduleTitle(),
                request.getSchStrDate().toString(),
                request.getSchEndDate().toString(),
                request.getScheduleCategory(),
                schState,
                request.getSchedulePayment(),
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
                request.getScheduleMemo()
        );
    }

    public void deleteSchedule(Long schNo) {
        scheduleRepository.deleteSchedule(schNo);
    }

}
