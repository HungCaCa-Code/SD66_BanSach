package org.example.datn.dto.order;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Integer sachId;
    private String tenSach;
    private String hinhAnh;
    private Integer soLuong;
    private BigDecimal donGia; // Giá tại thời điểm đặt hàng
}