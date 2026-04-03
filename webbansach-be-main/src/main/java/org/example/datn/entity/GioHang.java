package org.example.datn.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "Gio_Hang")
public class GioHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_gio_hang", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private NguoiDung nguoiDung;

    // === THÊM TRƯỜNG SACH BỊ THIẾU ===
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_sach", nullable = false)
    private Sach sach;
    // ==================================

    @ColumnDefault("1")
    @Column(name = "so_luong")
    private Integer soLuong;

    @ColumnDefault("getdate()")
    @Column(name = "ngay_them")
    private LocalDateTime ngayThem;
}