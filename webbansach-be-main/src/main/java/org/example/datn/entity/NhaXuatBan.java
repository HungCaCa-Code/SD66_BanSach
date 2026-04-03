package org.example.datn.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@Table(name = "Nha_Xuat_Ban")
public class NhaXuatBan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nxb", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "ten_nxb", nullable = false, length = 100)
    private String tenNxb;

    @Nationalized
    @Column(name = "dia_chi")
    private String diaChi;

    @Nationalized
    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

}