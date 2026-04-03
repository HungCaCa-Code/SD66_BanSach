package org.example.datn.repository;

import org.example.datn.entity.DanhGia;
import org.example.datn.entity.NguoiDung;
import org.example.datn.entity.Sach;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DanhGiaRepository extends JpaRepository<DanhGia, Integer> {

    // 1. Lấy tất cả đánh giá của 1 sách (có phân trang)
    Page<DanhGia> findBySach(Sach sach, Pageable pageable);

    // 2. Kiểm tra xem người dùng đã đánh giá sách này chưa (mỗi người 1 lần)
    boolean existsByNguoiDungAndSach(NguoiDung nguoiDung, Sach sach);
}