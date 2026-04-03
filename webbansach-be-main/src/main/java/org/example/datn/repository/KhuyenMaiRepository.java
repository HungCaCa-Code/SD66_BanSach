package org.example.datn.repository;

import org.example.datn.entity.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, Integer> {

    Optional<KhuyenMai> findByMaAndTrangThaiIsTrueAndNgayBatDauLessThanEqualAndNgayKetThucGreaterThanEqual(
            String ma,
            LocalDate ngayBatDau, // homNay
            LocalDate ngayKetThuc // homNay
    );

    // Lấy tất cả mã còn hoạt động VÀ còn hạn
    List<KhuyenMai> findAllByTrangThaiIsTrueAndNgayBatDauLessThanEqualAndNgayKetThucGreaterThanEqual(
            LocalDate ngayBatDau,
            LocalDate ngayKetThuc
    );
}