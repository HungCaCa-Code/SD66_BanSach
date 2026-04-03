package org.example.datn.dto.stats;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class GeneralStatsDTO {
    private Long totalOrders; // Tổng số đơn hàng
    private Long totalPendingOrders; // Đơn chờ xử lý
    private Long totalCustomers; // Tổng số khách hàng
    private BigDecimal totalRevenueAllTime; // Tổng doanh thu
}