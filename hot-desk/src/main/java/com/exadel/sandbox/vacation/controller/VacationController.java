package com.exadel.sandbox.vacation.controller;

import com.exadel.sandbox.vacation.dto.VacationCreateDto;
import com.exadel.sandbox.vacation.dto.VacationResponseDto;
import com.exadel.sandbox.vacation.dto.VacationUpdateDto;
import com.exadel.sandbox.vacation.service.VacationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("vacation")
@RequiredArgsConstructor
public class VacationController {
    private final VacationService vacationService;

    @GetMapping("list")
    ResponseEntity<List<VacationResponseDto>> getTgInfos() {
        return vacationService.getList();
    }

    @GetMapping("{id}")
    ResponseEntity<VacationResponseDto> getTgInfoById(@PathVariable("id") Long id) {
        return vacationService.getById(id);
    }

    @PostMapping()
    ResponseEntity<VacationResponseDto> createTgInfo(@Valid @RequestBody VacationCreateDto vacationCreateDto) {
        return vacationService.create(vacationCreateDto);
    }

    @DeleteMapping("{id}")
    void deleteTgInfo(@PathVariable("id") Long id) {
        vacationService.delete(id);
    }

    @PutMapping("{id}")
    ResponseEntity<VacationResponseDto> updateTgInfo(@PathVariable("id") Long id,
                                                     @Valid @RequestBody VacationUpdateDto vacationUpdateDto) {

        return vacationService.update(id, vacationUpdateDto);
    }

    @GetMapping("employee/{employeeId}")
    ResponseEntity<List<VacationResponseDto>> getByEmployeeID(@PathVariable Long employeeId){
        return vacationService.getByEmployeeId(employeeId);
    }
}
