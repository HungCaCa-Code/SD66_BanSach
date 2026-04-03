package org.example.datn.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopRevenueProductDTO {
    private Integer sachId;
    private String tieuDe;
    private BigDecimal totalRevenue; // Tổng doanh thu
}