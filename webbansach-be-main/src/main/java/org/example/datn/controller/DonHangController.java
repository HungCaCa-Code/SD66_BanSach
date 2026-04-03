package org.example.datn.controller;

import jakarta.validation.Valid;
import org.example.datn.dto.order.OrderDTO;
import org.example.datn.dto.order.OrderRequestDTO;
import org.example.datn.dto.order.OrderUpdateStatusRequestDTO;
import org.example.datn.service.DonHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class DonHangController {

    @Autowired
    private DonHangService donHangService;

    // ==============================
    // === API CHO KHÁCH HÀNG ===
    // ==============================

    // 1. Đặt hàng từ giỏ
    @PostMapping
    @PreAuthorize("hasRole('KHACH_HANG')")
    public ResponseEntity<OrderDTO> createOrder(
            @Valid @RequestBody OrderRequestDTO orderRequest
    ) throws UnsupportedEncodingException {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        OrderDTO order = donHangService.createOrderFromCart(username, orderRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    // 2. Lịch sử mua hàng
    @GetMapping("/my-history")
    @PreAuthorize("hasRole('KHACH_HANG')")
    public ResponseEntity<List<OrderDTO>> getMyOrderHistory() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        List<OrderDTO> orders = donHangService.getOrderHistoryForUser(username);

        return ResponseEntity.ok(orders);
    }

    // 3. Hủy đơn hàng
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('KHACH_HANG')")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Integer id) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        OrderDTO order = donHangService.cancelOrder(id, username);

        return ResponseEntity.ok(order);
    }

    // ==============================
    // === API CHO ADMIN ===
    // ==============================

    // 4. Xem tất cả đơn hàng (có phân trang)
    @GetMapping
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<Page<OrderDTO>> getAllOrders(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {

        Page<OrderDTO> orders = donHangService.getAllOrders(pageable);

        return ResponseEntity.ok(orders);
    }

    // 5. Xem chi tiết đơn hàng
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Integer id) {

        OrderDTO order = donHangService.getOrderById(id);

        return ResponseEntity.ok(order);
    }

    // 6. Cập nhật trạng thái đơn hàng
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('QUAN_TRI')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Integer id,
            @Valid @RequestBody OrderUpdateStatusRequestDTO request
    ) {

        OrderDTO updatedOrder = donHangService.updateOrderStatus(id, request);

        return ResponseEntity.ok(updatedOrder);
    }
}