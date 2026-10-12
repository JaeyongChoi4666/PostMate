package com.example.postmate.service;

import com.example.postmate.dto.ScheduleRequest;
import com.example.postmate.repository.ContractRepository;
import com.example.postmate.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiServiceValidationTests {
    private ScheduleRepository schedules;
    private ApiService service;

    @BeforeEach
    void setup() {
        schedules = mock(ScheduleRepository.class);
        service = new ApiService(mock(ContractRepository.class), schedules);
    }

    private ScheduleRequest request(String start, String end) {
        ScheduleRequest request = new ScheduleRequest();
        request.setScheduleNo(1L);
        request.setScheduleTitle("검증용 일감");
        request.setSchStrDate(LocalDate.parse(start));
        request.setSchEndDate(LocalDate.parse(end));
        request.setScheduleCategory("1");
        request.setScheduleState("11");
        request.setSchedulePayment(BigDecimal.ZERO);
        request.setPreTax(0);
        return request;
    }

    @Test
    void addingSameDayOrReversedDatesDoesNotWrite() {
        assertThrows(IllegalArgumentException.class, () -> service.addSchedule(request("2026-12-31", "2026-12-31")));
        assertThrows(IllegalArgumentException.class, () -> service.addSchedule(request("2027-01-01", "2026-12-31")));
        verifyNoInteractions(schedules);
    }

    @Test
    void nextDayAcrossYearBoundaryIsAccepted() {
        service.addSchedule(request("2026-12-31", "2027-01-01"));
        verify(schedules).addSchedule("검증용 일감", "2026-12-31", "2027-01-01", "1", "10", BigDecimal.ZERO, 0, null);
    }

    @Test
    void movingStartBeyondEndClampsEndToStart() {
        service.updateSchedule(request("2027-01-01", "2026-12-31"));
        verify(schedules).updateSchedule(1L, "검증용 일감", "2027-01-01", "2027-01-01", "1", "11", BigDecimal.ZERO, 0, null);
    }

    @Test
    void editingExistingSingleDayScheduleIsAccepted() {
        service.updateSchedule(request("2028-02-29", "2028-02-29"));
        verify(schedules).updateSchedule(1L, "검증용 일감", "2028-02-29", "2028-02-29", "1", "11", BigDecimal.ZERO, 0, null);
    }

    @Test
    void negativeRevenueDoesNotWrite() {
        ScheduleRequest request = request("2026-10-12", "2026-10-13");
        request.setSchedulePayment(BigDecimal.valueOf(-1));
        assertThrows(IllegalArgumentException.class, () -> service.addSchedule(request));
        verifyNoInteractions(schedules);
    }
}
