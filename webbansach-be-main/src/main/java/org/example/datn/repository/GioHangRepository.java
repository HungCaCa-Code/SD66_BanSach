package org.example.datn.repository;

import org.example.datn.entity.GioHang;
import org.example.datn.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Integer> {

    // Tìm giỏ hàng theo người dùng
    List<GioHang> findByNguoiDung(NguoiDung nguoiDung);

    // Xóa giỏ hàng theo người dùng (sau khi đặt hàng)
    void deleteByNguoiDung(NguoiDung nguoiDung);
}