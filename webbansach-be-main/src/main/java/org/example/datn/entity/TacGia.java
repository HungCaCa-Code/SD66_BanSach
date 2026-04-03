package org.example.datn.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "Tac_Gia")
public class TacGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tac_gia", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "ten_tac_gia", nullable = false, length = 100)
    private String tenTacGia;

    @Nationalized
    //@Lob
    @Column(name = "tieu_su")
    private String tieuSu;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

}