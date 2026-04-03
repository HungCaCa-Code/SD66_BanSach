package org.example.datn.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatsDTO {
    private String timePeriod; // (Ví dụ: "2025-11" hoặc "2025-11-05")
    private BigDecimal totalRevenue;
}