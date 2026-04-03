package org.example.datn.controller;

import jakarta.validation.Valid;
import org.example.datn.dto.TheLoaiDTO;
import org.example.datn.service.TheLoaiService;
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
@RequestMapping("/api/the-loai")
public class TheLoaiController {

    @Autowired
    private TheLoaiService theLoaiService;

    // Public
    @GetMapping
    public ResponseEntity<Page<TheLoaiDTO>> getAllTheLoai(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String keyword // <-- Thêm tham số này
    ) {
        // Truyền keyword vào service
        return ResponseEntity.ok(theLoaiService.getAllTheLoai(pageable, keyword));
    }

    // Public
    @GetMapping("/{id}")
    public ResponseEntity<TheLoaiDTO> getTheLoaiById(@PathVariable Integer id) {
        return ResponseEntity.ok(theLoaiService.getTheLoaiById(id));
    }

    // Admin only
    @PostMapping
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<TheLoaiDTO> createTheLoai(@Valid @RequestBody TheLoaiDTO theLoaiDTO) {
        return new ResponseEntity<>(theLoaiService.createTheLoai(theLoaiDTO), HttpStatus.CREATED);
    }

    // Admin only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<TheLoaiDTO> updateTheLoai(@PathVariable Integer id, @Valid @RequestBody TheLoaiDTO theLoaiDTO) {
        return ResponseEntity.ok(theLoaiService.updateTheLoai(id, theLoaiDTO));
    }

    // Admin only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<Void> deleteTheLoai(@PathVariable Integer id) {
        theLoaiService.deleteTheLoai(id);
        return ResponseEntity.noContent().build();
    }
}