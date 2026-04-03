package org.example.datn.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank
    private String tenDangNhap;
    @NotBlank
    private String matKhau;
}