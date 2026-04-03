package org.example.datn.dto.wishlist;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WishlistItemDTO {
    private Integer idYeuThich; // ID của chính mục yêu thích (để xóa)
    private Integer sachId;
    private String tenSach;
    private String hinhAnh;
    private BigDecimal gia;
}
