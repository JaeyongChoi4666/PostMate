package com.example.postmate.controller;

import com.example.postmate.dto.ContractRequest;
import com.example.postmate.service.ApiService;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiController {

    private ApiService apiService;

    //계약추가 ajax
    @PostMapping(value = "/addContract", consumes = "application/json")
    public int addContract(@RequestBody ContractRequest request) {

        return apiService.addContract(request);
    }
}
