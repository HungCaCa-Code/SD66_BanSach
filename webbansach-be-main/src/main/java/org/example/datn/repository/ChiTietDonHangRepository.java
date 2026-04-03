package org.example.datn.repository;

import org.example.datn.dto.stats.TopRevenueProductDTO;
import org.example.datn.entity.ChiTietDonHang;
import org.example.datn.dto.stats.TopSellingBookDTO; // Thêm
import org.springframework.data.domain.Pageable; // Thêm
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Thêm
import org.springframework.stereotype.Repository;
import java.util.List; // Thêm

@Repository // <-- Đảm bảo có @Repository
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Integer> {

    // === THÊM HÀM THỐNG KÊ ===

    // Lấy Top sách bán chạy (dựa trên đơn HOAN_THANH)
    @Query("SELECT new org.example.datn.dto.stats.TopSellingBookDTO(s.id, s.tieuDe, SUM(ct.soLuong)) " +
            "FROM ChiTietDonHang ct JOIN ct.sach s JOIN ct.donHang dh " +
            "WHERE dh.trangThai = 'HOAN_THANH' " +
            "GROUP BY s.id, s.tieuDe " +
            "ORDER BY SUM(ct.soLuong) DESC")
    List<TopSellingBookDTO> findTopSellingBooks(Pageable pageable);

    // Lấy Top sách Doanh thu cao nhất (dựa trên đơn HOAN_THANH)
    @Query("SELECT new org.example.datn.dto.stats.TopRevenueProductDTO(s.id, s.tieuDe, SUM(ct.soLuong * ct.donGia)) " +
            "FROM ChiTietDonHang ct JOIN ct.sach s JOIN ct.donHang dh " +
            "WHERE dh.trangThai = 'HOAN_THANH' " +
            "GROUP BY s.id, s.tieuDe " +
            "ORDER BY SUM(ct.soLuong * ct.donGia) DESC")
    List<TopRevenueProductDTO> findTopRevenueProducts(Pageable pageable);
}