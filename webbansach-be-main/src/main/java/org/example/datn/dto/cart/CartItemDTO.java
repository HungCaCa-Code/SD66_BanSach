package org.example.datn.dto.cart;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemDTO {
    private Integer idGioHang; // ID của chính mục giỏ hàng (để xóa)
    private Integer sachId;
    private String tenSach;
    private String hinhAnh;
    private BigDecimal gia;
    private Integer soLuong;
    private BigDecimal thanhTien; // (gia * soLuong)
}