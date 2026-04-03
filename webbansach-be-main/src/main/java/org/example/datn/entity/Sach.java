package org.example.datn.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Sach")
public class Sach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sach", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "tieu_de", nullable = false, length = 200)
    private String tieuDe;

    @ManyToOne
    @JoinColumn(name = "id_tac_gia")
    private TacGia tacGia;

    @ManyToOne
    @JoinColumn(name = "id_nxb")
    private NhaXuatBan nhaXuatBan;

    @Column(name = "nam_xuat_ban")
    private Integer namXuatBan;

    @Column(name = "gia", nullable = false, precision = 12, scale = 2)
    private BigDecimal gia;

    @ColumnDefault("0")
    @Column(name = "so_luong")
    private Integer soLuong;

    @Nationalized
    @Lob
    @Column(name = "mo_ta")
    private String moTa;

    @Nationalized
    @Column(name = "hinh_anh")
    private String hinhAnh;

    @ManyToOne
    @JoinColumn(name = "id_the_loai")
    private TheLoai theLoai;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    // 1. Liên kết tới Chi Tiết Đơn Hàng (để dùng cho findBestSelling)
    @OneToMany(mappedBy = "sach", fetch = FetchType.LAZY)
    private Set<ChiTietDonHang> chiTietDonHangs = new LinkedHashSet<>();

    // 2. Liên kết tới Đánh Giá (để dùng cho findHighestRated)
    @OneToMany(mappedBy = "sach", fetch = FetchType.LAZY)
    private Set<DanhGia> danhGias = new LinkedHashSet<>();

    // 3. Liên kết tới Yêu Thích (để dùng cho tính số lượt yêu thích)
    @OneToMany(mappedBy = "sach", fetch = FetchType.LAZY)
    private Set<YeuThich> yeuThiches = new LinkedHashSet<>();
}