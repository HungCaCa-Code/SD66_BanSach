package org.example.datn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size; // <-- THÊM IMPORT
import lombok.Data;

@Data
public class NhaXuatBanDTO {
    private Integer id;

    @NotBlank(message = "Tên nhà xuất bản không được để trống")
    @Size(max = 100, message = "Tên NXB không được vượt quá 100 ký tự")
    private String tenNxb;

    @Size(max = 255, message = "Địa chỉ quá dài")
    private String diaChi;

    @Size(max = 20, message = "Số điện thoại không hợp lệ")
    // Nếu muốn check kỹ hơn (ví dụ chỉ cho phép số):
    // @Pattern(regexp = "^[0-9+]*$", message = "Số điện thoại chỉ được chứa số và dấu +")
    private String soDienThoai;
}