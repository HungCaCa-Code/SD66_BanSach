package org.example.datn.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserProfileUpdateDTO {
    private String hoTen;

    @Email(message = "Email không hợp lệ")
    private String email;

    private String soDienThoai;
    private String diaChi;
}