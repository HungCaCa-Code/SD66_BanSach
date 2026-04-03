package org.example.datn.controller;

import org.example.datn.dto.stats.GeneralStatsDTO;
import org.example.datn.dto.stats.RevenueStatsDTO;
import org.example.datn.dto.stats.TopRevenueProductDTO;
import org.example.datn.dto.stats.TopSellingBookDTO;
import org.example.datn.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stats")
@PreAuthorize("hasRole('QUAN_TRI')") // Chỉ Admin được xem thống kê
public class StatsController {

    @Autowired
    private StatsService statsService;

    // 1. Lấy thống kê chung
    @GetMapping("/general")
    public ResponseEntity<GeneralStatsDTO> getGeneralStats() {
        return ResponseEntity.ok(statsService.getGeneralStats());
    }

    // 2. Lấy top 5 sách bán chạy
    @GetMapping("/top-selling-books")
    public ResponseEntity<List<TopSellingBookDTO>> getTopSellingBooks() {
        return ResponseEntity.ok(statsService.getTopSellingBooks());
    }

    // 3. Lấy doanh thu theo tháng
    @GetMapping("/monthly-revenue")
    public ResponseEntity<List<RevenueStatsDTO>> getMonthlyRevenue(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        return ResponseEntity.ok(statsService.getMonthlyRevenue(startDate, endDate));
    }

    // 4. Lấy doanh thu theo ngày (CÓ LỌC)
    @GetMapping("/daily-revenue")
    public ResponseEntity<List<RevenueStatsDTO>> getDailyRevenue(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        return ResponseEntity.ok(statsService.getDailyRevenue(startDate, endDate));
    }

    // 5. Lấy top 5 sách doanh thu cao
    @GetMapping("/top-revenue-products")
    public ResponseEntity<List<TopRevenueProductDTO>> getTopRevenueProducts() {
        return ResponseEntity.ok(statsService.getTopRevenueProducts());
    }

    // 6. Lấy doanh thu theo năm (Mới)
    @GetMapping("/yearly-revenue")
    public ResponseEntity<List<RevenueStatsDTO>> getYearlyRevenue(
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer endYear
    ) {
        return ResponseEntity.ok(statsService.getYearlyRevenue(startYear, endYear));
    }
}