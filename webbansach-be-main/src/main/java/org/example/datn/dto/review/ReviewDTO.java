package org.example.datn.dto.review;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewDTO {

    private Integer id;

    private String tenNguoiDung;

    private Integer diem;

    private String binhLuan;

    private LocalDateTime ngayTao;
}