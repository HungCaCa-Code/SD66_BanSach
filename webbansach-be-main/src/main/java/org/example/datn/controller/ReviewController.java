package org.example.datn.controller;

import jakarta.validation.Valid;
import org.example.datn.dto.review.ReviewDTO;
import org.example.datn.dto.review.ReviewRequestDTO;
import org.example.datn.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // 1. (Public) Lấy tất cả đánh giá của 1 sách (có phân trang)
    @GetMapping("/{sachId}")
    public ResponseEntity<Page<ReviewDTO>> getReviewsForBook(
            @PathVariable Integer sachId,
            @PageableDefault(size = 5, sort = "ngayTao") Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsForBook(sachId, pageable));
    }

    // 2. (Khách hàng) Gửi đánh giá mới
    @PostMapping
    @PreAuthorize("hasRole('KHACH_HANG')")
    public ResponseEntity<ReviewDTO> addReview(@Valid @RequestBody ReviewRequestDTO reviewRequest) {
        String tenNguoiDung = SecurityContextHolder.getContext().getAuthentication().getName();
        ReviewDTO newReview = reviewService.addReview(tenNguoiDung, reviewRequest);
        return new ResponseEntity<>(newReview, HttpStatus.CREATED);
    }

    // 3. (Khách hàng) Sửa đánh giá
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('KHACH_HANG')") // Chỉ tác giả (là KHACH_HANG) mới được sửa
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable("id") Integer reviewId,
            @Valid @RequestBody ReviewRequestDTO reviewRequest) {

        String tenNguoiDung = SecurityContextHolder.getContext().getAuthentication().getName();
        ReviewDTO updatedReview = reviewService.updateReview(reviewId, tenNguoiDung, reviewRequest);
        return ResponseEntity.ok(updatedReview);
    }

    // 4. (Khách hàng hoặc Admin) Xóa đánh giá
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Bất kỳ ai đăng nhập (cả KHÁCH HÀNG và ADMIN)
    public ResponseEntity<Void> deleteReview(@PathVariable("id") Integer reviewId) {
        String tenNguoiDung = SecurityContextHolder.getContext().getAuthentication().getName();
        reviewService.deleteReview(reviewId, tenNguoiDung);
        return ResponseEntity.noContent().build();
    }
}