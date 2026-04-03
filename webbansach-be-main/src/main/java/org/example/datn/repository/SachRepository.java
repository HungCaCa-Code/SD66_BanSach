package org.example.datn.repository;

import org.example.datn.entity.Sach;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SachRepository extends JpaRepository<Sach, Integer>, JpaSpecificationExecutor<Sach> {

    /**
     * Lấy sách BÁN CHẠY NHẤT:
     * 1. JOIN Sach (s) với ChiTietDonHang (ctdh)
     * 2. GROUP BY sách
     * 3. ORDER BY SUM(ctdh.soLuong) DESC (Tổng số lượng bán giảm dần)
     */
    @Query("SELECT s FROM Sach s LEFT JOIN s.chiTietDonHangs ctdh " +
            "WHERE ctdh.donHang.trangThai = 'HOAN_THANH' " + // Thêm: Chỉ tính đơn đã hoàn thành
            "GROUP BY s.id, s.tieuDe, s.tacGia, s.nhaXuatBan, s.namXuatBan, s.gia, s.soLuong, s.moTa, s.hinhAnh, s.theLoai, s.ngayTao " +
            "HAVING SUM(ctdh.soLuong) > 0 " + // <-- THÊM DÒNG NÀY
            "ORDER BY SUM(ctdh.soLuong) DESC, s.tieuDe ASC")
    Page<Sach> findBestSelling(Pageable pageable);

    /**
     * Lấy sách ĐÁNH GIÁ CAO NHẤT:
     * 1. JOIN Sach (s) với DanhGia (dg)
     * 2. GROUP BY sách
     * 3. ORDER BY AVG(dg.diem) DESC (Điểm trung bình giảm dần)
     */
    @Query("SELECT s FROM Sach s LEFT JOIN s.danhGias dg " +
            "GROUP BY s.id, s.tieuDe, s.tacGia, s.nhaXuatBan, s.namXuatBan, s.gia, s.soLuong, s.moTa, s.hinhAnh, s.theLoai, s.ngayTao " +
            "HAVING COUNT(dg.id) > 0 " + // <-- THÊM DÒNG NÀY
            "ORDER BY AVG(dg.diem) DESC, s.tieuDe ASC")
    Page<Sach> findHighestRated(Pageable pageable);

    // 1. Check trùng Combo (Tiêu đề + Tác giả + NXB) cho TẠO MỚI
    // Spring Data JPA tự động hiểu tên hàm dài ngoằng này!
    boolean existsByTieuDeAndTacGiaIdAndNhaXuatBanId(String tieuDe, Integer tacGiaId, Integer nxbId);

    // 2. Check trùng Combo cho CẬP NHẬT (trừ chính nó ra)
    boolean existsByTieuDeAndTacGiaIdAndNhaXuatBanIdAndIdNot(String tieuDe, Integer tacGiaId, Integer nxbId, Integer id);

    /**
     * Lấy sách YÊU THÍCH NHẤT:
     * 1. JOIN Sach (s) với YeuThich (yt)
     * 2. GROUP BY sách
     * 3. ORDER BY COUNT(yt.id) DESC (Tổng số lượt thích giảm dần)
     */
    @Query("SELECT s FROM Sach s JOIN s.yeuThiches yt " + // <-- Đổi LEFT JOIN thành JOIN (Cách 1)
            // HOẶC "SELECT s FROM Sach s LEFT JOIN s.yeuThiches yt " + (Cách 2)
            "GROUP BY s.id, s.tieuDe, s.tacGia, s.nhaXuatBan, s.namXuatBan, s.gia, s.soLuong, s.moTa, s.hinhAnh, s.theLoai, s.ngayTao " +
            // "HAVING COUNT(yt.id) > 0 " + // <-- Thêm dòng này (Cách 2)
            "ORDER BY COUNT(yt.id) DESC, s.tieuDe ASC")
    Page<Sach> findMostFavorited(Pageable pageable);
}
