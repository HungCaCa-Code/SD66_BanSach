package org.example.datn.dto.order;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class OrderRequestDTO {

    // 1. Mã khuyến mãi (có thể null)
    private String maKhuyenMai;

    // 2. Hình thức thanh toán (FE gửi "COD" hoặc "VNPAY")
    //@NotBlank(message = "Vui lòng chọn hình thức thanh toán")
    private String hinhThucThanhToan;

    // 3. Thông tin người nhận (bắt buộc)
    //@NotBlank(message = "Họ tên người nhận không được để trống")
    private String hoTenNguoiNhan;

    //@NotBlank(message = "Số điện thoại người nhận không được để trống")
    private String sdtNguoiNhan;

    //@NotBlank(message = "Địa chỉ người nhận không được để trống")
    private String diaChiGiaoHang;
}