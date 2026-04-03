package org.example.datn.controller;

import jakarta.validation.Valid;
import org.example.datn.dto.NhaXuatBanDTO;
import org.example.datn.service.NhaXuatBanService;
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
@RequestMapping("/api/nxb")
public class NhaXuatBanController {

    @Autowired
    private NhaXuatBanService nhaXuatBanService;

    // Public
    @GetMapping
    public ResponseEntity<Page<NhaXuatBanDTO>> getAllNxb(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String keyword
    ) {
        // Truyền keyword vào service
        return ResponseEntity.ok(nhaXuatBanService.getAllNxb(pageable, keyword));
    }

    // Public
    @GetMapping("/{id}")
    public ResponseEntity<NhaXuatBanDTO> getNxbById(@PathVariable Integer id) {
        return ResponseEntity.ok(nhaXuatBanService.getNxbById(id));
    }

    // Admin only
    @PostMapping
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<NhaXuatBanDTO> createNxb(@Valid @RequestBody NhaXuatBanDTO nxbDTO) {
        return new ResponseEntity<>(nhaXuatBanService.createNxb(nxbDTO), HttpStatus.CREATED);
    }

    // Admin only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<NhaXuatBanDTO> updateNxb(@PathVariable Integer id, @Valid @RequestBody NhaXuatBanDTO nxbDTO) {
        return ResponseEntity.ok(nhaXuatBanService.updateNxb(id, nxbDTO));
    }

    // Admin only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<Void> deleteNxb(@PathVariable Integer id) {
        nhaXuatBanService.deleteNxb(id);
        return ResponseEntity.noContent().build();
    }
}