package org.example.datn.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private String jwt;
    private String tenDangNhap;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String diaChi;
    private String vaiTro; // "admin" hoặc "user"
    private LocalDateTime ngayTao;
}