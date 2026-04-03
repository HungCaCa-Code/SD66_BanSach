package org.example.datn.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequestDTO {
    @NotNull(message = "ID Sách không được để trống")
    private Integer sachId;

    @NotNull(message = "Điểm không được để trống")
    @Min(value = 1, message = "Điểm phải từ 1-5")
    @Max(value = 5, message = "Điểm phải từ 1-5")
    private Integer diem;

    @NotBlank(message = "Bình luận không được để trống")
    private String binhLuan;
}