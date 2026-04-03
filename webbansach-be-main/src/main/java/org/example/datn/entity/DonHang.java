package org.example.datn.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

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
@Table(name = "Don_Hang")
public class DonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_don_hang", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_dat")
    private LocalDateTime ngayDat;

    /*@Size(max = 20)*/
    @Enumerated(EnumType.STRING)
    @Nationalized
    @ColumnDefault("'CHO_XAC_NHAN'")
    @Column(name = "trang_thai", length = 20)
    private TrangThaiDonHang trangThai;

    @Column(name = "tong_tien", precision = 12, scale = 2)
    private BigDecimal tongTien;

//    @Nationalized
//    @Column(name = "hinh_thuc_thanh_toan", length = 50)
//    private String hinhThucThanhToan;
@OneToOne(mappedBy = "donHang", cascade = CascadeType.ALL)
private ThanhToan thanhToan;

    @Nationalized
    @Column(name = "ho_ten_nguoi_nhan", length = 100)
    private String hoTenNguoiNhan;

    @Nationalized
    @Column(name = "sdt_nguoi_nhan", length = 20)
    private String sdtNguoiNhan;

    @Nationalized
    @Column(name = "dia_chi_giao_hang")
    private String diaChiGiaoHang;

    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChiTietDonHang> chiTietDonHangs = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "Don_Hang_Khuyen_Mai",
            joinColumns = @JoinColumn(name = "id_don_hang"),
            inverseJoinColumns = @JoinColumn(name = "id_khuyen_mai"))
    private Set<KhuyenMai> khuyenMais = new LinkedHashSet<>();
}