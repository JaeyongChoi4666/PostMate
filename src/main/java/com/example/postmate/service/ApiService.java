package com.example.postmate.service;

import com.example.postmate.dto.ContractRequest;
import com.example.postmate.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApiService {

    private final ContractRepository contractRepository;

    public int addContract(ContractRequest request) {
        return contractRepository.insertContract(
                request.getContractTitle(),
                request.getStrDate().toString(),
                request.getEndDate().toString(),
                request.getContractCategory(),
                request.getContractPayment(),
                request.getContractChannel()
                );
    }
}
