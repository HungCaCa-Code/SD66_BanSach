package org.example.datn.controller;

import org.example.datn.dto.PublicKhuyenMaiDTO;
import org.example.datn.service.KhuyenMaiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public-promotions")
public class PublicPromotionController {

    @Autowired
    private KhuyenMaiService khuyenMaiService;

    /**
     * API cho ComboBox (Lấy tất cả mã còn hạn)
     * FE gọi API này để hiển thị danh sách cho khách chọn
     */
    @GetMapping("/available")
    public ResponseEntity<List<PublicKhuyenMaiDTO>> getAvailablePromotions() {
        return ResponseEntity.ok(khuyenMaiService.getAvailablePromotions());
    }

    /**
     * API cho ô "Nhập mã" / "Tìm kiếm"
     * FE gọi API này khi khách nhập tay để check xem mã có xịn không
     */
    @GetMapping("/check/{maCode}")
    public ResponseEntity<PublicKhuyenMaiDTO> checkPromotion(@PathVariable String maCode) {
        return ResponseEntity.ok(khuyenMaiService.checkPromotion(maCode));
    }
}