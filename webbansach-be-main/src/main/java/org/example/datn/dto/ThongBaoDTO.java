package org.example.datn.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ThongBaoDTO {
    private Integer id;
    private String noiDung;
    private LocalDateTime ngayTao;
    private Boolean daDoc;
    // Không cần trả về NguoiDung nữa, vì người xem chính là chủ sở hữu
}