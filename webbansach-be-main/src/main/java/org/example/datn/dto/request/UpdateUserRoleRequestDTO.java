package org.example.datn.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRoleRequestDTO {

    @NotBlank(message = "Vai trò không được để trống")
    // Sẽ nhận giá trị là "KHACH_HANG" hoặc "QUAN_TRI"
    private String vaiTro;
}