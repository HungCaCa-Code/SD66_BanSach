package org.example.datn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TheLoaiDTO {
    private Integer id;

    @NotBlank(message = "Tên thể loại không được để trống")
    @Size(max = 100, message = "Tên thể loại không được vượt quá 100 ký tự")
    private String tenTheLoai;

    @Size(max = 255, message = "Mô tả thể loại không được vượt quá 255 ký tự")
    private String moTa;
}