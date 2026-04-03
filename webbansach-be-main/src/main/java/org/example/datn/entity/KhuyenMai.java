package org.example.datn.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Khuyen_Mai")
public class KhuyenMai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_khuyen_mai", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "ma", nullable = false, length = 50, unique = true)
    private String ma;

    @Nationalized
    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "phan_tram")
    private Integer phanTram; // (ví dụ: 10 = 10%)

    @Column(name = "ngay_bat_dau")
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @Column(name = "trang_thai")
    private Boolean trangThai = true; // Mặc định là true (hoạt động)

    // Xử lý bảng DON_HANG_KHUYEN_MAI
    @ManyToMany(mappedBy = "khuyenMais")
    private Set<DonHang> donHangs = new LinkedHashSet<>();
}