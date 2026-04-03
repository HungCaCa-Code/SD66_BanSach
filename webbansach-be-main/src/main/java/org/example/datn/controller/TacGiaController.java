package org.example.datn.controller;

import jakarta.validation.Valid;
import org.example.datn.dto.TacGiaDTO;
import org.example.datn.service.TacGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tac-gia")
public class TacGiaController {

    @Autowired
    private TacGiaService tacGiaService;

    // Public
    @GetMapping
    public ResponseEntity<Page<TacGiaDTO>> getAllTacGias(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String keyword // <-- Thêm tham số này
    ) {
        // Truyền keyword vào service
        return ResponseEntity.ok(tacGiaService.getAllTacGias(pageable, keyword));
    }

    // Public
    @GetMapping("/{id}")
    public ResponseEntity<TacGiaDTO> getTacGiaById(@PathVariable Integer id) {
        return ResponseEntity.ok(tacGiaService.getTacGiaById(id));
    }

    // Admin only
    @PostMapping
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<TacGiaDTO> createTacGia(@Valid @RequestBody TacGiaDTO tacGiaDTO) {
        return new ResponseEntity<>(tacGiaService.createTacGia(tacGiaDTO), HttpStatus.CREATED);
    }

    // Admin only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<TacGiaDTO> updateTacGia(@PathVariable Integer id, @Valid @RequestBody TacGiaDTO tacGiaDTO) {
        return ResponseEntity.ok(tacGiaService.updateTacGia(id, tacGiaDTO));
    }

    // Admin only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<Void> deleteTacGia(@PathVariable Integer id) {
        tacGiaService.deleteTacGia(id);
        return ResponseEntity.noContent().build();
    }
}