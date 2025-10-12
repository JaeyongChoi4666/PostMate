package com.example.postmate.controller;

import com.example.postmate.dto.ContractRequest;
import com.example.postmate.dto.ScheduleRequest;
import com.example.postmate.service.ApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
public class ApiController {

    private final ApiService apiService;

    //계약추가
    @PostMapping(value = "/addContract")
    public ResponseEntity<Integer> addContract(@RequestBody ContractRequest request) {
        log.info("POST /addContract called. payload={}", request);
        try {
            int result = apiService.addContract(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("addContract failed", e);
            // 클라이언트 콘솔에서 xhr.status, xhr.responseText로 바로 확인 가능
            return ResponseEntity.internalServerError().build();
        }
    }

    //계약검색
    @GetMapping(value = "/searchContract")
    public ResponseEntity<?> searchContract(@RequestParam(required = false) String conTitle,@RequestParam String allYn) {
        if(allYn.equals("Y")) {
            return ResponseEntity.ok(apiService.searchContractAll(conTitle));
        }else{
            return ResponseEntity.ok(apiService.searchContract(conTitle));
        }
    }

    //계약삭제
    @DeleteMapping(value = "/deleteContract")
    public ResponseEntity<?> deleteContract(@RequestParam Long conId) {
        try {
            apiService.deleteContract(conId);
            return ResponseEntity.ok("삭제 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패: " + e.getMessage());
        }
    }

    //일정종류 불러오기
    @GetMapping(value = "/loadScheduleCategory")
    public ResponseEntity<?> loadScheduleCategory() {
        return ResponseEntity.ok(apiService.loadScheduleCategory());
    }

    //일정추가
    @PostMapping(value = "/addSchedule")
    public ResponseEntity<Integer> addSchedule(@RequestBody ScheduleRequest request) {
        log.info("POST /addSchedule called. payload={}", request);
        try {
            int result = apiService.addSchedule(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("addSchedule failed", e);
            // 클라이언트 콘솔에서 xhr.status, xhr.responseText로 바로 확인 가능
            return ResponseEntity.internalServerError().build();
        }
    }

    //일정검색
    @GetMapping(value = "/searchSchedule")
    public ResponseEntity<?> searchSchedule(@RequestParam String schStrDate,@RequestParam String schEndDate) {
        return ResponseEntity.ok(apiService.searchSchedule(schStrDate,schEndDate));
    }
}
