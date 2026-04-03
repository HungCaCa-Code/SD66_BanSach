package org.example.datn.controller;

import org.example.datn.dto.payment.PaymentResDTO;
import org.example.datn.entity.DonHang;
import org.example.datn.entity.ThanhToan;
import org.example.datn.entity.TrangThaiDonHang;
import org.example.datn.repository.DonHangRepository;
import org.example.datn.repository.ThanhToanRepository;
import org.example.datn.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private DonHangRepository donHangRepository; // Thêm
    @Autowired
    private ThanhToanRepository thanhToanRepository; // Thêm

    // 1. Tạo link thanh toán (Sửa để nhận orderId)
    @GetMapping("/create_payment")
    public ResponseEntity<?> createPayment(
            @RequestParam Integer orderId, // <-- Bắt buộc phải có orderId
            @RequestParam long amount,
            @RequestParam(required = false) String returnUrl
    ) throws UnsupportedEncodingException {
        // Gọi service với orderId
        PaymentResDTO paymentResDTO = paymentService.createVnPayPayment(orderId, amount, null, returnUrl);
        return ResponseEntity.ok(paymentResDTO);
    }

    // 2. Xử lý kết quả trả về từ VNPAY
    @GetMapping("/vnpay_return")
    @Transactional // Để đảm bảo cập nhật DB an toàn
    public ResponseEntity<?> vnpayReturn(
            @RequestParam(value = "vnp_ResponseCode") String responseCode,
            @RequestParam(value = "vnp_TxnRef") String orderIdStr, // Lấy lại orderId
            @RequestParam(value = "vnp_Amount") String amountStr     // Lấy số tiền đã thanh toán
    ) {
        try {
            if ("00".equals(responseCode)) { // "00" là thành công
                // 1. Tìm đơn hàng
                Integer orderId = Integer.parseInt(orderIdStr);
                DonHang donHang = donHangRepository.findById(orderId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + orderId));

                // 2. Tạo bản ghi Thanh Toán
                ThanhToan thanhToan = new ThanhToan();
                thanhToan.setDonHang(donHang);
                thanhToan.setPhuongThuc("VNPAY");
                thanhToan.setNgayThanhToan(LocalDateTime.now());
                // VNPAY trả về số tiền nhân 100, nên phải chia 100 để về đúng giá trị
                thanhToan.setSoTien(new BigDecimal(amountStr).divide(new BigDecimal(100)));
                thanhToan.setTrangThai("THANH_CONG");
                thanhToanRepository.save(thanhToan);

                // 3. Cập nhật trạng thái đơn hàng (Ví dụ: Đã xác nhận vì đã trả tiền)
                // [Lưu ý: Nếu bro muốn thêm trạng thái DA_THANH_TOAN thì phải sửa cả Enum và SQL CHECK]
                donHang.setTrangThai(TrangThaiDonHang.DA_XAC_NHAN);
                donHangRepository.save(donHang);

                return ResponseEntity.ok("Thanh toán thành công! Đơn hàng #" + orderId + " đã được xác nhận.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thanh toán thất bại. Mã lỗi VNPAY: " + responseCode);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi xử lý thanh toán: " + e.getMessage());
        }
    }
}