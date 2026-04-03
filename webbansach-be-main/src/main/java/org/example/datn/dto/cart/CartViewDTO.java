package org.example.datn.dto.cart;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartViewDTO {
    private List<CartItemDTO> items;
    private BigDecimal tongTien; // Tổng tiền của tất cả item
}