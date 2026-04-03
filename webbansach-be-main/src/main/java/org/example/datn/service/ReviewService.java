package org.example.datn.service;

import org.example.datn.dto.review.ReviewDTO;
import org.example.datn.dto.review.ReviewRequestDTO;
import org.example.datn.entity.*;
import org.example.datn.exception.ResourceNotFoundException;
import org.example.datn.repository.DanhGiaRepository;
import org.example.datn.repository.DonHangRepository;
import org.example.datn.repository.NguoiDungRepository;
import org.example.datn.repository.SachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ReviewService {

    @Autowired
    private DanhGiaRepository danhGiaRepository;
    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    @Autowired
    private SachRepository sachRepository;
    @Autowired
    private DonHangRepository donHangRepository;

    // 1. (Public) Lấy đánh giá cho 1 sách
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsForBook(Integer sachId, Pageable pageable) {
        Sach sach = sachRepository.findById(sachId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));

        Page<DanhGia> reviews = danhGiaRepository.findBySach(sach, pageable);
        return reviews.map(this::mapToDTO);
    }

    // 2. (Khách hàng) Thêm đánh giá mới
    @Transactional
    public ReviewDTO addReview(String tenNguoiDung, ReviewRequestDTO dto) {
        NguoiDung user = getUser(tenNguoiDung);
        Sach sach = sachRepository.findById(dto.getSachId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));

        // Kiểm tra 1: User đã đánh giá sách này chưa?
        if (danhGiaRepository.existsByNguoiDungAndSach(user, sach)) {
            throw new IllegalArgumentException("Bạn đã đánh giá sách này rồi");
        }

        // Kiểm tra 2 (Quan trọng): User đã MUA và HOÀN THÀNH đơn hàng chứa sách này chưa?
        boolean hasPurchased = donHangRepository.hasUserPurchasedBook(user, sach, TrangThaiDonHang.HOAN_THANH);
        if (!hasPurchased) {
            throw new SecurityException("Bạn phải mua và hoàn thành đơn hàng sách này mới được đánh giá");
        }

        // Nếu qua 2 kiểm tra -> Tạo đánh giá
        DanhGia review = new DanhGia();
        review.setNguoiDung(user);
        review.setSach(sach);
        review.setDiem(dto.getDiem());
        review.setBinhLuan(dto.getBinhLuan());
        review.setNgayTao(LocalDateTime.now());

        return mapToDTO(danhGiaRepository.save(review));
    }

    // Hàm tiện ích
    private NguoiDung getUser(String tenNguoiDung) {
        return nguoiDungRepository.findByTenDangNhap(tenNguoiDung)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    // Hàm tiện ích
    private ReviewDTO mapToDTO(DanhGia review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setTenNguoiDung(review.getNguoiDung().getHoTen()); // Hiển thị tên
        dto.setDiem(review.getDiem());
        dto.setBinhLuan(review.getBinhLuan());
        dto.setNgayTao(review.getNgayTao());
        return dto;
    }

    // 3. (Khách hàng) Cập nhật đánh giá
    @Transactional
    public ReviewDTO updateReview(Integer reviewId, String tenNguoiDung, ReviewRequestDTO dto) {
        NguoiDung user = getUser(tenNguoiDung);
        DanhGia review = danhGiaRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));

        // Kiểm tra 1: User này có phải là tác giả của đánh giá không?
        if (!review.getNguoiDung().getId().equals(user.getId())) {
            throw new AccessDeniedException("Bạn không có quyền sửa đánh giá này");
        }

        // Kiểm tra 2 (Tùy chọn): Sách có khớp không
        if (!review.getSach().getId().equals(dto.getSachId())) {
            throw new IllegalArgumentException("Đánh giá này không thuộc về sách này");
        }

        // Cập nhật
        review.setDiem(dto.getDiem());
        review.setBinhLuan(dto.getBinhLuan());
        // (Không cập nhật ngayTao)

        return mapToDTO(danhGiaRepository.save(review));
    }

    // 4. (Khách hàng hoặc Admin) Xóa đánh giá
    @Transactional
    public void deleteReview(Integer reviewId, String tenNguoiDung) {
        NguoiDung user = getUser(tenNguoiDung);
        DanhGia review = danhGiaRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá"));

        // Kiểm tra: Một là bạn là TÁC GIẢ, hai là bạn là ADMIN
        boolean isAuthor = review.getNguoiDung().getId().equals(user.getId());
        boolean isAdmin = user.getVaiTro().equals("QUAN_TRI"); //

        if (!isAuthor && !isAdmin) {
            throw new AccessDeniedException("Bạn không có quyền xóa đánh giá này");
        }

        danhGiaRepository.delete(review);
    }
}