package org.example.datn.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.datn.dto.SachDTO;
import org.example.datn.service.SachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import java.math.BigDecimal;
import java.util.Set;

@RestController
@RequestMapping("/api/sach")
public class SachController {

    @Autowired
    private SachService sachService;

    // Inject ObjectMapper để chuyển JSON string thành Object
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Validator validator;

    // Create - Chỉ Admin
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<SachDTO> createSach(
            @RequestPart("sach") String sachJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        try {
            SachDTO sachDTO = objectMapper.readValue(sachJson, SachDTO.class);
            Set<ConstraintViolation<SachDTO>> violations = validator.validate(sachDTO);
            if (!violations.isEmpty()) throw new ConstraintViolationException(violations);

            SachDTO created = sachService.createSach(sachDTO, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JSON không hợp lệ", e);
        }
    }

    // Read (Get by ID)
    @GetMapping("/{id}")
    public ResponseEntity<SachDTO> getSachById(@PathVariable Integer id) {
        SachDTO sachDTO = sachService.getSachById(id);
        return ResponseEntity.ok(sachDTO);
    }

    // Read (Get all with Pagination)
    @GetMapping
    public ResponseEntity<Page<SachDTO>> getAllSach(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String sortBy,

            // === THÊM CÁC THAM SỐ LỌC MỚI ===
            @RequestParam(required = false) Integer theLoaiId,
            @RequestParam(required = false) Integer tacGiaId,
            @RequestParam(required = false) Integer nxbId,
            @RequestParam(required = false) BigDecimal giaTu,
            @RequestParam(required = false) BigDecimal giaDen,
            @RequestParam(required = false) String keyword
            // =================================
    ) {
        // Truyền tất cả vào Service
        Page<SachDTO> sachPage = sachService.getAllSach(
                pageable, sortBy, theLoaiId, tacGiaId, nxbId, giaTu, giaDen, keyword
        );
        return ResponseEntity.ok(sachPage);
    }

    // Update - Chỉ Admin
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<SachDTO> updateSach(
            @PathVariable Integer id,
            @RequestPart("sach") String sachJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        try {
            SachDTO sachDTO = objectMapper.readValue(sachJson, SachDTO.class);
            Set<ConstraintViolation<SachDTO>> violations = validator.validate(sachDTO);
            if (!violations.isEmpty()) throw new ConstraintViolationException(violations);

            SachDTO updated = sachService.updateSach(id, sachDTO, file);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dữ liệu JSON không hợp lệ", e);
        }
    }

    // Delete - Chỉ Admin
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<Void> deleteSach(@PathVariable Integer id) {
        sachService.deleteSach(id);
        return ResponseEntity.noContent().build();
    }
}