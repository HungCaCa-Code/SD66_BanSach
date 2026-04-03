package org.example.datn.controller;

import jakarta.validation.Valid;
import org.example.datn.dto.cart.AddToCartRequestDTO;
import org.example.datn.dto.cart.CartItemDTO;
import org.example.datn.dto.cart.CartViewDTO;
import org.example.datn.service.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('KHACH_HANG')") // Chỉ khách hàng mới có giỏ hàng
public class GioHangController {

    @Autowired
    private GioHangService gioHangService;

    // Lấy giỏ hàng của tôi
    @GetMapping
    public ResponseEntity<CartViewDTO> getMyCart() {
        String tenNguoiDung = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(gioHangService.getCart(tenNguoiDung));
    }

    // Thêm vào giỏ hàng
    @PostMapping
    public ResponseEntity<CartItemDTO> addToMyCart(@Valid @RequestBody AddToCartRequestDTO request) {
        String tenNguoiDung = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(gioHangService.addToCart(tenNguoiDung, request));
    }

    // Xóa 1 mục khỏi giỏ hàng
    @DeleteMapping("/{idGioHang}")
    public ResponseEntity<Void> removeFromMyCart(@PathVariable Integer idGioHang) {
        String tenNguoiDung = SecurityContextHolder.getContext().getAuthentication().getName();
        gioHangService.removeFromCart(tenNguoiDung, idGioHang);
        return ResponseEntity.noContent().build();
    }

    // Cập nhật số lượng mục trong giỏ hàng
    @PutMapping("/{idGioHang}")
    public ResponseEntity<CartItemDTO> updateCartItem(
            @PathVariable Integer idGioHang,
            @RequestParam Integer soLuong // Nhận số lượng mới từ tham số URL
    ) {
        String tenNguoiDung = SecurityContextHolder.getContext().getAuthentication().getName();
        CartItemDTO updatedItem = gioHangService.updateCartItem(tenNguoiDung, idGioHang, soLuong);

        if (updatedItem == null) {
            return ResponseEntity.noContent().build(); // Nếu số lượng <=0 -> Xóa -> Trả về 204
        }
        return ResponseEntity.ok(updatedItem);
    }

    // API cho nút (+)
    @PutMapping("/{idGioHang}/increase")
    public ResponseEntity<CartItemDTO> increaseQuantity(@PathVariable Integer idGioHang) {
        String tenNguoiDung = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(gioHangService.increaseQuantity(tenNguoiDung, idGioHang));
    }

    // API cho nút (-)
    @PutMapping("/{idGioHang}/decrease")
    public ResponseEntity<CartItemDTO> decreaseQuantity(@PathVariable Integer idGioHang) {
        String tenNguoiDung = SecurityContextHolder.getContext().getAuthentication().getName();
        CartItemDTO updatedItem = gioHangService.decreaseQuantity(tenNguoiDung, idGioHang);

        if (updatedItem == null) {
            return ResponseEntity.noContent().build(); // Đã xóa khỏi giỏ
        }
        return ResponseEntity.ok(updatedItem);
    }
}