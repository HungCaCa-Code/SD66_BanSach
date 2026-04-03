package org.example.datn.dto.order;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Integer id;

    // Thông tin người đặt (chủ tài khoản)
    private String tenNguoiDung;
    private String emailNguoiDung;
    private String sdtNguoiDung;
    private String diaChiNguoiDung;

    private LocalDateTime ngayDat;
    private String trangThai; // (CHO_XAC_NHAN, DA_XAC_NHAN, ...)
    private BigDecimal tongTien;
    private List<OrderItemDTO> chiTietDonHangs;
    private String paymentUrl;

    // Thông tin người nhận (lưu lúc đặt)
    private String hoTenNguoiNhan;
    private String sdtNguoiNhan;
    private String diaChiGiaoHang;
}