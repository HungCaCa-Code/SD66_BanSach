package org.example.datn.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class KhuyenMaiDTO {
    private Integer id;

    @NotBlank(message = "Mã khuyến mãi không được để trống")
    private String ma;

    private String moTa;

    @NotNull(message = "Phần trăm không được để trống")
    @Min(value = 1, message = "Phần trăm phải lớn hơn 0")
    @Max(value = 100, message = "Phần trăm không được lớn hơn 100")
    private Integer phanTram;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate ngayBatDau;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate ngayKetThuc;

    private Boolean trangThai;

    private String trangThaiHienTai;
}