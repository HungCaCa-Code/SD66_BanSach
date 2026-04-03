package org.example.datn.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SachDTO {
    private Integer idSach;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(min = 1, max = 200, message = "Tiêu đề phải từ 1 đến 200 ký tự")
    @Pattern(regexp = "^[^<>]*$", message = "Tiêu đề không được chứa ký tự không hợp lệ")
    private String tieuDe;

    @NotNull(message = "Vui lòng chọn Tác giả")
    private Integer idTacGia;
    private String tenTacGia;

    @NotNull(message = "Vui lòng chọn Nhà xuất bản")
    private Integer idNxb;
    private String tenNxb;

    @NotNull(message = "Năm xuất bản không được để trống")
    @Min(value = 1900, message = "Năm xuất bản không hợp lệ (phải từ năm 1900)")
    @Max(value = 2100, message = "Năm xuất bản không được vượt quá năm hiện tại") // Tạm thời để 2100, tốt nhất là check động trong Service
    private Integer namXuatBan;

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    @Digits(integer = 10, fraction = 2, message = "Giá trị tiền không hợp lệ") // Tối đa 10 số phần nguyên, 2 số phần thập phân
    private BigDecimal gia;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không được âm")
    @Max(value = 99999, message = "Số lượng nhập quá lớn")
    private Integer soLuong;

    @Size(max = 4000, message = "Mô tả không được vượt quá 4000 ký tự")
    private String moTa;

    private String hinhAnh;

    @NotNull(message = "Vui lòng chọn Thể loại")
    private Integer idTheLoai;
    private String tenTheLoai;

    private LocalDateTime ngayTao;
}