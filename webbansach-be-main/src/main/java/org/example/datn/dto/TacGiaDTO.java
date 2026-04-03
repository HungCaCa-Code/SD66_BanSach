package org.example.datn.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class TacGiaDTO {
    private Integer id;

    @NotBlank(message = "Tên tác giả không được để trống")
    @Size(max = 100, message = "Tên tác giả không được vượt quá 100 ký tự")
    @Pattern(regexp = "^(?!\\d+$).+", message = "Tên tác giả không được chỉ chứa số")
    private String tenTacGia;

    @Size(max = 4000, message = "Tiểu sử quá dài")
    private String tieuSu;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)

    private LocalDate ngaySinh;
}