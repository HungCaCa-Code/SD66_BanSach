package org.example.datn.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopSellingBookDTO {
    private Integer sachId;
    private String tieuDe;
    private Long totalSold; // Tổng số lượng đã bán
}