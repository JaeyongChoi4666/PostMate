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
        when(schedules.countVisibleCategory("1")).thenReturn(1);
        when(schedules.countCategoryState("1", "11")).thenReturn(1);
        when(schedules.initialState("1")).thenReturn("10");
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
    void addingReversedDatesDoesNotWrite() {
        assertThrows(IllegalArgumentException.class, () -> service.addSchedule(request("2027-01-01", "2026-12-31")));
        verifyNoInteractions(schedules);
    }

    @Test
    void sameDayFiveHundredWonScheduleCanBeAdded() {
        ScheduleRequest request = request("2026-12-31", "2026-12-31");
        request.setSchedulePayment(BigDecimal.valueOf(500));
        service.addSchedule(request);
        verify(schedules).addSchedule("검증용 일감", "2026-12-31", "2026-12-31", "1", "11", BigDecimal.valueOf(500), 0, null);
    }

    @Test
    void hundredWonScheduleCanBeUpdated() {
        ScheduleRequest request = request("2028-02-29", "2028-02-29");
        request.setSchedulePayment(BigDecimal.valueOf(100));
        service.updateSchedule(request);
        verify(schedules).updateSchedule(1L, "검증용 일감", "2028-02-29", "2028-02-29", "1", "11", BigDecimal.valueOf(100), 0, null);
    }

    @Test
    void nonHundredWonAmountIsRejected() {
        ScheduleRequest request = request("2026-12-31", "2026-12-31");
        request.setSchedulePayment(BigDecimal.valueOf(550));
        assertThrows(IllegalArgumentException.class, () -> service.addSchedule(request));
        assertThrows(IllegalArgumentException.class, () -> service.updateSchedule(request));
        verifyNoInteractions(schedules);
    }

    @Test
    void nextDayAcrossYearBoundaryIsAccepted() {
        service.addSchedule(request("2026-12-31", "2027-01-01"));
        verify(schedules).addSchedule("검증용 일감", "2026-12-31", "2027-01-01", "1", "11", BigDecimal.ZERO, 0, null);
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

    @Test
    void threadsCanBeCreatedWithEachState() {
        when(schedules.countVisibleCategory("8")).thenReturn(1);
        for (String state : new String[]{"80", "81", "82"}) {
            when(schedules.countCategoryState("8", state)).thenReturn(1);
            ScheduleRequest request = request("2026-10-13", "2026-10-13");
            request.setScheduleCategory("8");
            request.setScheduleState(state);
            service.addSchedule(request);
            service.updateSchedule(request);
            verify(schedules).addSchedule("검증용 일감", "2026-10-13", "2026-10-13", "8", state, BigDecimal.ZERO, 0, null);
            verify(schedules).updateSchedule(1L, "검증용 일감", "2026-10-13", "2026-10-13", "8", state, BigDecimal.ZERO, 0, null);
        }
    }

    @Test
    void threadsDefaultToUploadPendingForOlderClients() {
        when(schedules.countVisibleCategory("8")).thenReturn(1);
        when(schedules.initialState("8")).thenReturn("80");
        ScheduleRequest request = request("2026-10-13", "2026-10-13");
        request.setScheduleCategory("8");
        request.setScheduleState(null);
        service.addSchedule(request);
        verify(schedules).addSchedule("검증용 일감", "2026-10-13", "2026-10-13", "8", "80", BigDecimal.ZERO, 0, null);
    }

    @Test
    void hiddenCategoriesCannotBeUsedForNewSchedules() {
        ScheduleRequest request = request("2026-10-13", "2026-10-13");
        request.setScheduleCategory("2");
        assertThrows(IllegalArgumentException.class, () -> service.addSchedule(request));
        verify(schedules, never()).addSchedule(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void historicalHiddenCategoryCanBeRetainedButCannotBeAssignedToAnotherSchedule() {
        ScheduleRequest request = request("2026-10-13", "2026-10-13");
        request.setScheduleCategory("2");
        request.setScheduleState("21");
        when(schedules.countExistingCategory(1L, "2")).thenReturn(1);
        when(schedules.countCategoryState("2", "21")).thenReturn(1);
        service.updateSchedule(request);
        verify(schedules).updateSchedule(1L, "검증용 일감", "2026-10-13", "2026-10-13", "2", "21", BigDecimal.ZERO, 0, null);
        request.setScheduleNo(2L);
        assertThrows(IllegalArgumentException.class, () -> service.updateSchedule(request));
    }

    @Test
    void mismatchedCategoryStateCannotBeSaved() {
        ScheduleRequest request = request("2026-10-13", "2026-10-13");
        request.setScheduleState("80");
        assertThrows(IllegalArgumentException.class, () -> service.addSchedule(request));
        assertThrows(IllegalArgumentException.class, () -> service.updateSchedule(request));
    }
}
