package org.example.datn.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@Table(name = "The_Loai")
public class TheLoai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_the_loai", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "ten_the_loai", nullable = false, length = 100)
    private String tenTheLoai;

    @Nationalized
    @Column(name = "mo_ta")
    private String moTa;

}