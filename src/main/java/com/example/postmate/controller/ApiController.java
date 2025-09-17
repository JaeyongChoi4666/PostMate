package com.example.postmate.controller;

import com.example.postmate.dto.ContractRequest;
import com.example.postmate.service.ApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
public class ApiController {

    private final ApiService apiService;

    //계약추가 ajax
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

    @GetMapping(value = "/searchContact")
    public ResponseEntity<String> searchContact(@RequestParam(required = false) String conTitle) {
        return ResponseEntity.ok(apiService.searchContract(conTitle).toString());
    }
}
