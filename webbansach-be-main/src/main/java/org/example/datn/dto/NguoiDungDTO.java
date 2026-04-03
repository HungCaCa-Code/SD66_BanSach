package org.example.datn.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NguoiDungDTO {
    private Integer id;
    private String tenDangNhap;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String diaChi;
    private String vaiTro;
    private LocalDateTime ngayTao;
}