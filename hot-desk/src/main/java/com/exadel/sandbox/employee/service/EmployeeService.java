package com.exadel.sandbox.employee.service;

import com.exadel.sandbox.base.BaseCrudService;
import com.exadel.sandbox.booking.entity.Booking;
import com.exadel.sandbox.booking.entity.BookingDates;
import com.exadel.sandbox.booking.repository.BookingRepository;
import com.exadel.sandbox.booking.service.BookingService;
import com.exadel.sandbox.employee.dto.employeeDto.EmployeeCreateDto;
import com.exadel.sandbox.employee.dto.employeeDto.EmployeeResponseDto;
import com.exadel.sandbox.employee.dto.employeeDto.EmployeeUpdateDto;
import com.exadel.sandbox.employee.entity.Employee;
import com.exadel.sandbox.employee.repository.EmployeeRepository;
import com.exadel.sandbox.employee.repository.TgInfoRepository;
import com.exadel.sandbox.exception.exceptions.EntityNotFoundException;
import com.exadel.sandbox.role.entity.Role;
import com.exadel.sandbox.role.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class EmployeeService extends BaseCrudService<Employee, EmployeeResponseDto, EmployeeUpdateDto, EmployeeCreateDto, EmployeeRepository> {

    private final RoleRepository roleRepository;
    private final BookingRepository bookingRepository;

    public EmployeeService(ModelMapper mapper, EmployeeRepository repository, TgInfoRepository tgInfoRepository, RoleRepository roleRepository, BookingRepository bookingRepository) {
        super(mapper, repository);
        this.roleRepository = roleRepository;
        this.bookingRepository = bookingRepository;
    }

    public ResponseEntity<EmployeeResponseDto> addRole(Long id, Long roleId) {

        Employee employee = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Employee with id: " + id + " not found"));
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new EntityNotFoundException("Role with id: " + id + " not found"));

        employee.getRoles().add(role);

        EmployeeResponseDto employeeResponseDto = mapper.map(repository.save(employee), EmployeeResponseDto.class);
        return ResponseEntity.ok(employeeResponseDto);
    }

    public ResponseEntity<EmployeeResponseDto> getByChatId(String chatId) {
        Employee employee = repository.findEmployeeByTgInfoChatId(chatId).orElseThrow(() -> new EntityNotFoundException("Employee with id: " + chatId + " not found"));

        return ResponseEntity.ok(mapper.map(employee, EmployeeResponseDto.class));
    }

    public ResponseEntity<List<LocalDate>> getEmployeeBookedDates(Long id) {
        LocalDate today = LocalDate.now();
        LocalDate finishLimit = today.plusMonths(BookingService.MAX_MONTH);

        List<LocalDate> employeeBookedDates = repository.findEmployeeBookedDates(id, today, finishLimit);

        return ResponseEntity.ok(employeeBookedDates);
    }

    public ResponseEntity<List<LocalDate>> getEmployeeBookedDatesAll(Long id) {
        List<LocalDate> employeeBookedDates = repository.findEmployeeBookedDates(id);

        return ResponseEntity.ok(employeeBookedDates);
    }

    @Override
    public ResponseEntity<EmployeeResponseDto> update(Long id, EmployeeUpdateDto employeeUpdateDto) {
        LocalDate employmentEnd = employeeUpdateDto.getEmploymentEnd().toLocalDate();

        if (employmentEnd != null){
            List<Booking> bookingsByEmployeeId = bookingRepository.findBookingsByEmployeeId(id);
            bookingsByEmployeeId.forEach(booking -> {
                List<BookingDates> shouldBeSaved = new ArrayList<>();
                booking.getDates().forEach(date -> {
                    if (employmentEnd.isAfter(date.getDate()))
                        shouldBeSaved.add(date);
                });
                booking.setDates(shouldBeSaved);
                bookingRepository.save(booking);
            });
        }

        return super.update(id, employeeUpdateDto);
    }
}
