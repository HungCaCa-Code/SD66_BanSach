package org.example.datn.controller;

import jakarta.validation.Valid;
import org.example.datn.dto.KhuyenMaiDTO;
import org.example.datn.service.KhuyenMaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotions")
@PreAuthorize("hasRole('QUAN_TRI')")
public class KhuyenMaiController {

    @Autowired
    private KhuyenMaiService khuyenMaiService;

    @GetMapping
    public ResponseEntity<Page<KhuyenMaiDTO>> getAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(khuyenMaiService.getAll(pageable));
    }

    @PostMapping
    public ResponseEntity<KhuyenMaiDTO> create(@Valid @RequestBody KhuyenMaiDTO dto) {
        return new ResponseEntity<>(khuyenMaiService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KhuyenMaiDTO> update(@PathVariable Integer id, @Valid @RequestBody KhuyenMaiDTO dto) {
        return ResponseEntity.ok(khuyenMaiService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        khuyenMaiService.delete(id);
        return ResponseEntity.noContent().build();
    }
}