package org.example.datn.repository;

import org.example.datn.dto.stats.RevenueStatsDTO;
import org.example.datn.entity.DonHang;
import org.example.datn.entity.NguoiDung;
import org.example.datn.entity.Sach;
import org.example.datn.entity.TrangThaiDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Integer> {

    // Tìm đơn hàng của 1 người dùng (cho "Lịch sử mua hàng")
    List<DonHang> findByNguoiDungOrderByIdDesc(NguoiDung nguoiDung);

    // Kiểm tra xem người dùng đã mua sách này VÀ đơn hàng đã hoàn thành chưa
    @Query("SELECT COUNT(dh) > 0 FROM DonHang dh JOIN dh.chiTietDonHangs ctdh " +
            "WHERE dh.nguoiDung = :user " +
            "AND ctdh.sach = :sach " +
            "AND dh.trangThai = :trangThai")
    boolean hasUserPurchasedBook(
            @Param("user") NguoiDung user,
            @Param("sach") Sach sach,
            @Param("trangThai") TrangThaiDonHang trangThai
    );

    // === THÊM CÁC HÀM THỐNG KÊ ===

    // 1. Đếm số đơn hàng theo trạng thái
    long countByTrangThai(TrangThaiDonHang trangThai);

    // 2. Tính tổng doanh thu (chỉ tính đơn HOAN_THANH)
    @Query("SELECT SUM(dh.tongTien) FROM DonHang dh WHERE dh.trangThai = 'HOAN_THANH'")
    BigDecimal findTotalRevenueAllTime();

    // 3. Tính doanh thu theo tháng (chỉ tính đơn HOAN_THANH)
    @Query("SELECT new org.example.datn.dto.stats.RevenueStatsDTO(FORMAT(dh.ngayDat, 'yyyy-MM'), SUM(dh.tongTien)) " +
            "FROM DonHang dh " +
            "WHERE dh.trangThai = 'HOAN_THANH' " +
            "  AND CAST(dh.ngayDat AS DATE) >= :startDate " + // Thêm
            "  AND CAST(dh.ngayDat AS DATE) <= :endDate " +   // Thêm
            "GROUP BY FORMAT(dh.ngayDat, 'yyyy-MM') " +
            "ORDER BY FORMAT(dh.ngayDat, 'yyyy-MM') DESC")
    List<RevenueStatsDTO> findMonthlyRevenueByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // 4. Tính doanh thu theo NGÀY (CÓ LỌC THEO KHOẢNG THỜI GIAN)
    @Query("SELECT new org.example.datn.dto.stats.RevenueStatsDTO(FORMAT(dh.ngayDat, 'yyyy-MM-dd'), SUM(dh.tongTien)) " +
            "FROM DonHang dh " +
            "WHERE dh.trangThai = 'HOAN_THANH' " +
            "  AND CAST(dh.ngayDat AS DATE) >= :startDate " + // Lọc ngày BĐ
            "  AND CAST(dh.ngayDat AS DATE) <= :endDate " +   // Lọc ngày KT
            "GROUP BY FORMAT(dh.ngayDat, 'yyyy-MM-dd') " +
            "ORDER BY FORMAT(dh.ngayDat, 'yyyy-MM-dd') DESC")
    List<RevenueStatsDTO> findDailyRevenueByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // 5. Tính doanh thu theo NĂM (CẬP NHẬT)
    @Query("SELECT new org.example.datn.dto.stats.RevenueStatsDTO(FORMAT(dh.ngayDat, 'yyyy'), SUM(dh.tongTien)) " +
            "FROM DonHang dh " +
            "WHERE dh.trangThai = 'HOAN_THANH' " +
            "  AND YEAR(dh.ngayDat) >= :startYear " + // Thêm
            "  AND YEAR(dh.ngayDat) <= :endYear " +   // Thêm
            "GROUP BY FORMAT(dh.ngayDat, 'yyyy') " +
            "ORDER BY FORMAT(dh.ngayDat, 'yyyy') DESC")
    List<RevenueStatsDTO> findYearlyRevenueByYearRange(
            @Param("startYear") Integer startYear,
            @Param("endYear") Integer endYear
    );


}