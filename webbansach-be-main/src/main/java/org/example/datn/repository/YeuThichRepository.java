package org.example.datn.repository;

import org.example.datn.entity.NguoiDung;
import org.example.datn.entity.Sach;
import org.example.datn.entity.YeuThich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YeuThichRepository extends JpaRepository<YeuThich, Integer> {

    // 1. Tìm tất cả mục yêu thích của 1 người dùng
    List<YeuThich> findByNguoiDung(NguoiDung nguoiDung);

    // 2. Tìm một mục yêu thích cụ thể (để kiểm tra xem đã thích hay chưa)
    Optional<YeuThich> findByNguoiDungAndSach(NguoiDung nguoiDung, Sach sach);
}
