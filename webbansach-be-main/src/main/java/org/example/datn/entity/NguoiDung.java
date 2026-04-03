package org.example.datn.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Nguoi_Dung")
public class NguoiDung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nguoi_dung", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "ten_dang_nhap", nullable = false, length = 50)
    private String tenDangNhap;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "mat_khau", nullable = false)
    private String matKhau;

    @Size(max = 100)
    @Nationalized
    @Column(name = "ho_ten", length = 100)
    private String hoTen;

    @Size(max = 100)
    @Nationalized
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 20)
    @Nationalized
    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Size(max = 255)
    @Nationalized
    @Column(name = "dia_chi")
    private String diaChi;

    @Size(max = 20)
    @Nationalized
    @ColumnDefault("'KHACH_HANG'")
    @Column(name = "vai_tro", length = 20)
    private String vaiTro;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @OneToMany(mappedBy = "nguoiDung")
    private Set<DanhGia> danhGias = new LinkedHashSet<>();

    @OneToMany(mappedBy = "nguoiDung")
    private Set<DonHang> donHangs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "nguoiDung")
    private Set<GioHang> gioHangs = new LinkedHashSet<>();

    @OneToMany(mappedBy = "nguoiDung")
    private Set<NhatKy> nhatKIES = new LinkedHashSet<>();

    @OneToMany(mappedBy = "nguoiDung")
    private Set<ThongBao> thongBaos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "nguoiDung")
    private Set<YeuThich> yeuThiches = new LinkedHashSet<>();

}