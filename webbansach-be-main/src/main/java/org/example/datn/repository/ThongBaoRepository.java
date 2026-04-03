package org.example.datn.repository;

import org.example.datn.entity.NguoiDung;
import org.example.datn.entity.ThongBao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThongBaoRepository extends JpaRepository<ThongBao, Integer> {
    // Lấy thông báo của một user, sắp xếp mới nhất lên đầu
    List<ThongBao> findByNguoiDungOrderByNgayTaoDesc(NguoiDung nguoiDung);
}