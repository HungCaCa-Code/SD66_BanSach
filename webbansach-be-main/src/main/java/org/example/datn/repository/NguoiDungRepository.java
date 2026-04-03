package org.example.datn.repository;

import org.example.datn.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {

    // Thêm hàm này để UserDetailsServiceImpl có thể tìm user
    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);
    Optional<NguoiDung> findByEmail(String email);

    // === THÊM HÀM THỐNG KÊ ===

    // Đếm tổng số khách hàng
    long countByVaiTro(String vaiTro);
}
