package org.example.datn.controller;

import jakarta.validation.Valid;
import org.example.datn.dto.NguoiDungDTO;
import org.example.datn.dto.request.UpdateUserRoleRequestDTO;
import org.example.datn.dto.UserProfileUpdateDTO;
import org.example.datn.service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private NguoiDungService nguoiDungService;

    // Lấy thông tin của chính người dùng đang đăng nhập
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()") // Yêu cầu phải đăng nhập
    public ResponseEntity<NguoiDungDTO> getMyProfile() {
        // Lấy tenDangNhap từ token
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(nguoiDungService.getUserProfile(tenDangNhap));
    }

    // Cập nhật thông tin của chính người dùng đang đăng nhập
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()") // Yêu cầu phải đăng nhập
    public ResponseEntity<NguoiDungDTO> updateMyProfile(@Valid @RequestBody UserProfileUpdateDTO profileUpdate) {
        String tenDangNhap = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(nguoiDungService.updateUserProfile(tenDangNhap, profileUpdate));
    }

    /**
     * [ADMIN] Lấy tất cả người dùng (phân trang)
     */
    @GetMapping
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<Page<NguoiDungDTO>> getAllUsers(
            @PageableDefault(size = 10, sort = "ngayTao") Pageable pageable) {
        return ResponseEntity.ok(nguoiDungService.getAllUsers(pageable));
    }

    /**
     * [ADMIN] Cập nhật vai trò của 1 người dùng
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<NguoiDungDTO> updateUserRole(
            @PathVariable("id") Integer userId,
            @Valid @RequestBody UpdateUserRoleRequestDTO roleRequest) {

        NguoiDungDTO updatedUser = nguoiDungService.updateUserRole(userId, roleRequest.getVaiTro());
        return ResponseEntity.ok(updatedUser);
    }
}