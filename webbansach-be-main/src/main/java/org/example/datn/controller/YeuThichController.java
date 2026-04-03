package org.example.datn.controller;

import org.example.datn.dto.wishlist.WishlistItemDTO;
import org.example.datn.service.YeuThichService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@PreAuthorize("hasRole('KHACH_HANG')") // Chỉ khách hàng mới có Yêu thích
public class YeuThichController {

    @Autowired
    private YeuThichService yeuThichService;

    // 1. Lấy danh sách yêu thích của tôi
    @GetMapping("/me")
    public ResponseEntity<List<WishlistItemDTO>> getMyWishlist() {
        String tenNguoiDung = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(yeuThichService.getMyWishlist(tenNguoiDung));
    }

    // 2. Thêm/Bỏ thích (Toggle)
    @PostMapping("/{sachId}")
    public ResponseEntity<?> toggleWishlist(@PathVariable Integer sachId) {
        String tenNguoiDung = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean added = yeuThichService.toggleWishlistItem(tenNguoiDung, sachId);
        if (added) {
            return ResponseEntity.ok().body("Đã thêm vào yêu thích");
        } else {
            return ResponseEntity.ok().body("Đã xóa khỏi yêu thích");
        }
    }
}