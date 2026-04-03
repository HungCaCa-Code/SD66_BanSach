package org.example.datn.service;

import org.example.datn.dto.stats.GeneralStatsDTO;
import org.example.datn.dto.stats.RevenueStatsDTO;
import org.example.datn.dto.stats.TopRevenueProductDTO;
import org.example.datn.dto.stats.TopSellingBookDTO;
import org.example.datn.entity.TrangThaiDonHang;
import org.example.datn.repository.ChiTietDonHangRepository;
import org.example.datn.repository.DonHangRepository;
import org.example.datn.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
public class StatsService {

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;

    // 1. Lấy thống kê chung (cho Dashboard)
    public GeneralStatsDTO getGeneralStats() {
        GeneralStatsDTO stats = new GeneralStatsDTO();

        stats.setTotalOrders(donHangRepository.count());
        stats.setTotalPendingOrders(donHangRepository.countByTrangThai(TrangThaiDonHang.CHO_XAC_NHAN));
        stats.setTotalCustomers(nguoiDungRepository.countByVaiTro("KHACH_HANG"));

        BigDecimal totalRevenue = donHangRepository.findTotalRevenueAllTime();
        stats.setTotalRevenueAllTime(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        return stats;
    }

    // 2. Lấy Top 5 sách bán chạy nhất
    public List<TopSellingBookDTO> getTopSellingBooks() {
        // Lấy top 5
        return chiTietDonHangRepository.findTopSellingBooks(PageRequest.of(0, 5));
    }

    // 3. Lấy doanh thu theo tháng
    public List<RevenueStatsDTO> getMonthlyRevenue(LocalDate startDate, LocalDate endDate) {

        // Logic xử lý nếu FE không gửi ngày
        if (endDate == null) {
            endDate = LocalDate.now(); // Mặc định là hôm nay
        }
        if (startDate == null) {
            // Mặc định là 12 tháng trước, tính từ ngày 1
            startDate = endDate.minusMonths(12).withDayOfMonth(1);
        }

        return donHangRepository.findMonthlyRevenueByDateRange(startDate, endDate);
    }

    // 4. Lấy doanh thu theo ngày (CÓ LỌC)
    public List<RevenueStatsDTO> getDailyRevenue(LocalDate startDate, LocalDate endDate) {

        // Logic xử lý nếu FE không gửi ngày
        if (endDate == null) {
            endDate = LocalDate.now(); // Mặc định là hôm nay
        }
        if (startDate == null) {
            // Mặc định là 30 ngày trước
            startDate = endDate.minusDays(30);
        }

        // Gọi hàm Repository mới (sẽ tạo ở Bước 3)
        return donHangRepository.findDailyRevenueByDateRange(startDate, endDate);
    }

    // 5. Lấy Top 5 sách doanh thu cao nhất
    public List<TopRevenueProductDTO> getTopRevenueProducts() {
        // Lấy top 5
        return chiTietDonHangRepository.findTopRevenueProducts(PageRequest.of(0, 5));
    }

    // 6. Lấy doanh thu theo năm (Mới)
    public List<RevenueStatsDTO> getYearlyRevenue(Integer startYear, Integer endYear) {
        int currentYear = Year.now().getValue();

        if (endYear == null) {
            endYear = currentYear;
        }
        if (startYear == null) {
            startYear = endYear - 4; // Mặc định lấy 5 năm (ví dụ: 2025-4 = 2021)
        }

        return donHangRepository.findYearlyRevenueByYearRange(startYear, endYear);
    }
}