package org.example.datn.service;

import org.example.datn.entity.DonHang;
import org.example.datn.entity.ThanhToan;
import org.example.datn.repository.ThanhToanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ThanhToanService {

    @Autowired
    private ThanhToanRepository thanhToanRepository;

    /**
     * Tự động tạo một giao dịch "Tiền mặt" (COD) khi đơn hàng hoàn thành.
     * @param donHang Đơn hàng vừa được chuyển sang 'HOAN_THANH'
     */
//    @Transactional
//    public void createCodPayment(DonHang donHang) {
//        ThanhToan payment = new ThanhToan();
//        payment.setDonHang(donHang);
//        payment.setPhuongThuc("TIEN_MAT"); //
//        payment.setNgayThanhToan(LocalDateTime.now());
//        payment.setSoTien(donHang.getTongTien()); //
//        payment.setTrangThai("THANH_CONG"); //
//
//        thanhToanRepository.save(payment);
//    }

    @Transactional
    public void createPayment(DonHang donHang, String method) {

        // ===== COD =====
        if ("COD".equalsIgnoreCase(method)) {
            // KHÔNG tạo ngay
            // Chỉ tạo khi đơn hàng HOÀN THÀNH
            return;
        }

        // ===== VNPAY =====
        if ("VNPAY".equalsIgnoreCase(method)) {
            ThanhToan payment = new ThanhToan();

            payment.setDonHang(donHang);
            payment.setPhuongThuc("VNPAY");
            payment.setNgayThanhToan(LocalDateTime.now());
            payment.setSoTien(donHang.getTongTien());

            // Chưa thanh toán xong → pending
            payment.setTrangThai("CHO_THANH_TOAN");

            thanhToanRepository.save(payment);
        }
    }

    /**
     * Tạo thanh toán COD khi đơn hàng hoàn thành
     */
    @Transactional
    public void createCodPayment(DonHang donHang) {

        // Kiểm tra đã tồn tại chưa (tránh duplicate)
        boolean exists = thanhToanRepository.existsByDonHang(donHang);

        if (exists) return;

        ThanhToan payment = new ThanhToan();

        payment.setDonHang(donHang);
        payment.setPhuongThuc("TIEN_MAT");
        payment.setNgayThanhToan(LocalDateTime.now());
        payment.setSoTien(donHang.getTongTien());

        // COD hoàn thành luôn
        payment.setTrangThai("THANH_CONG");

        thanhToanRepository.save(payment);
    }

    /**
     * Callback từ VNPAY → update trạng thái
     */
    @Transactional
    public void updatePaymentStatus(Integer orderId, String status) {

        ThanhToan payment = thanhToanRepository
                .findByDonHang_Id(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy payment"));

        payment.setTrangThai(status);

        thanhToanRepository.save(payment);
    }
}