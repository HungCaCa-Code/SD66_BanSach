package org.example.datn.service;

import org.example.datn.dto.order.OrderDTO;
import org.example.datn.dto.order.OrderItemDTO;
import org.example.datn.dto.order.OrderRequestDTO;
import org.example.datn.dto.order.OrderUpdateStatusRequestDTO;
import org.example.datn.dto.payment.PaymentResDTO;
import org.example.datn.entity.*;
import org.example.datn.exception.ResourceNotFoundException;
import org.example.datn.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonHangService {

    @Autowired
    private DonHangRepository donHangRepository;
    @Autowired
    private GioHangRepository gioHangRepository;
    @Autowired
    private NguoiDungRepository nguoiDungRepository;
    @Autowired
    private SachRepository sachRepository;
    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;
    @Autowired
    private ThanhToanService thanhToanService;
    @Autowired
    private ThongBaoService thongBaoService;
    @Autowired
    private PaymentService paymentService;

    @Transactional
    public OrderDTO createOrderFromCart(String tenNguoiDung, OrderRequestDTO orderRequest)
            throws UnsupportedEncodingException {

        NguoiDung user = getUser(tenNguoiDung);
        List<GioHang> cartItems = gioHangRepository.findByNguoiDung(user);

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng rỗng");
        }

        DonHang donHang = new DonHang();
        donHang.setNguoiDung(user);
        donHang.setNgayDat(LocalDateTime.now());
        donHang.setTrangThai(TrangThaiDonHang.CHO_XAC_NHAN);

        // ===== XỬ LÝ PAYMENT METHOD =====
        String paymentMethod = "COD";
        if ("VNPAY".equalsIgnoreCase(orderRequest.getHinhThucThanhToan())) {
            paymentMethod = "VNPAY";
        }

        // ===== THÔNG TIN NGƯỜI NHẬN =====
        String hoTenNhan = orderRequest.getHoTenNguoiNhan();
        String sdtNhan = orderRequest.getSdtNguoiNhan();
        String diaChiNhan = orderRequest.getDiaChiGiaoHang();

        if (hoTenNhan == null || hoTenNhan.isBlank()) hoTenNhan = user.getHoTen();
        if (sdtNhan == null || sdtNhan.isBlank()) sdtNhan = user.getSoDienThoai();
        if (diaChiNhan == null || diaChiNhan.isBlank()) diaChiNhan = user.getDiaChi();

        donHang.setHoTenNguoiNhan(hoTenNhan);
        donHang.setSdtNguoiNhan(sdtNhan);
        donHang.setDiaChiGiaoHang(diaChiNhan);

        BigDecimal tongTienGoc = BigDecimal.ZERO;

        // ===== GIỎ HÀNG =====
        for (GioHang cartItem : cartItems) {
            Sach sach = cartItem.getSach();

            if (sach.getSoLuong() < cartItem.getSoLuong()) {
                throw new IllegalArgumentException("Không đủ hàng: " + sach.getTieuDe());
            }

            sach.setSoLuong(sach.getSoLuong() - cartItem.getSoLuong());
            sachRepository.save(sach);

            ChiTietDonHang ct = new ChiTietDonHang();
            ct.setDonHang(donHang);
            ct.setSach(sach);
            ct.setSoLuong(cartItem.getSoLuong());
            ct.setDonGia(sach.getGia());

            donHang.getChiTietDonHangs().add(ct);

            tongTienGoc = tongTienGoc.add(
                    sach.getGia().multiply(BigDecimal.valueOf(cartItem.getSoLuong()))
            );
        }

        // ===== KHUYẾN MÃI =====
        BigDecimal tongTienCuoi = tongTienGoc;

        if (orderRequest.getMaKhuyenMai() != null) {
            LocalDate now = LocalDate.now();

            KhuyenMai km = khuyenMaiRepository
                    .findByMaAndTrangThaiIsTrueAndNgayBatDauLessThanEqualAndNgayKetThucGreaterThanEqual(
                            orderRequest.getMaKhuyenMai(), now, now)
                    .orElseThrow(() -> new IllegalArgumentException("Mã KM không hợp lệ"));

            BigDecimal discount = tongTienGoc
                    .multiply(BigDecimal.valueOf(km.getPhanTram()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            tongTienCuoi = tongTienGoc.subtract(discount);

            donHang.getKhuyenMais().add(km);
        }

        donHang.setTongTien(tongTienCuoi);

        DonHang saved = donHangRepository.save(donHang);

        // ===== TẠO PAYMENT =====
        thanhToanService.createPayment(saved, paymentMethod);

        gioHangRepository.deleteByNguoiDung(user);

        OrderDTO dto = mapToOrderDTO(saved);

        // ===== VNPAY =====
        if ("VNPAY".equalsIgnoreCase(paymentMethod)) {
            PaymentResDTO paymentRes = paymentService.createVnPayPayment(
                    saved.getId(),
                    saved.getTongTien().longValue(),
                    "Thanh toan don hang #" + saved.getId(),
                    null
            );
            dto.setPaymentUrl(paymentRes.getUrl());
        }

        return dto;
    }

    // ===== GIỮ NGUYÊN CÁC HÀM KHÁC =====

    private NguoiDung getUser(String tenNguoiDung) {
        return nguoiDungRepository.findByTenDangNhap(tenNguoiDung)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    private OrderDTO mapToOrderDTO(DonHang donHang) {
        OrderDTO dto = new OrderDTO();
        dto.setId(donHang.getId());
        dto.setNgayDat(donHang.getNgayDat());
        dto.setTrangThai(donHang.getTrangThai().name());
        dto.setTongTien(donHang.getTongTien());

        dto.setHoTenNguoiNhan(donHang.getHoTenNguoiNhan());
        dto.setSdtNguoiNhan(donHang.getSdtNguoiNhan());
        dto.setDiaChiGiaoHang(donHang.getDiaChiGiaoHang());

        NguoiDung user = donHang.getNguoiDung();
        dto.setTenNguoiDung(user.getHoTen());
        dto.setEmailNguoiDung(user.getEmail());
        dto.setSdtNguoiDung(user.getSoDienThoai());
        dto.setDiaChiNguoiDung(user.getDiaChi());

        dto.setChiTietDonHangs(
                donHang.getChiTietDonHangs().stream().map(ct -> {
                    OrderItemDTO i = new OrderItemDTO();
                    i.setSachId(ct.getSach().getId());
                    i.setTenSach(ct.getSach().getTieuDe());
                    i.setHinhAnh(ct.getSach().getHinhAnh());
                    i.setSoLuong(ct.getSoLuong());
                    i.setDonGia(ct.getDonGia());
                    return i;
                }).collect(Collectors.toList())
        );

        return dto;
    }

    // ==============================
// 2. Lịch sử mua hàng
// ==============================
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrderHistoryForUser(String tenNguoiDung) {
        NguoiDung user = getUser(tenNguoiDung);

        return donHangRepository.findByNguoiDungOrderByIdDesc(user)
                .stream()
                .map(this::mapToOrderDTO)
                .collect(Collectors.toList());
    }

    // ==============================
// 3. Admin - xem tất cả đơn
// ==============================
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        return donHangRepository.findAll(pageable)
                .map(this::mapToOrderDTO);
    }

    // ==============================
// 4. Xem chi tiết đơn
// ==============================
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Integer id) {
        DonHang donHang = donHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        return mapToOrderDTO(donHang);
    }

    // ==============================
// 5. Update trạng thái
// ==============================
    @Transactional
    public OrderDTO updateOrderStatus(Integer id, OrderUpdateStatusRequestDTO request) {

        DonHang donHang = donHangRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        try {
            TrangThaiDonHang newStatus =
                    TrangThaiDonHang.valueOf(request.getTrangThai().toUpperCase());

            donHang.setTrangThai(newStatus);

            // Nếu hoàn thành → tạo thanh toán COD
            if (newStatus == TrangThaiDonHang.HOAN_THANH) {
                thanhToanService.createPayment(donHang, "COD");
            }

            // Gửi thông báo
            thongBaoService.sendNotification(
                    donHang.getNguoiDung(),
                    "Đơn hàng #" + id + " -> " + newStatus
            );

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ");
        }

        return mapToOrderDTO(donHangRepository.save(donHang));
    }

    // ==============================
// 6. Hủy đơn hàng
// ==============================
    @Transactional
    public OrderDTO cancelOrder(Integer orderId, String tenNguoiDung) {

        NguoiDung user = getUser(tenNguoiDung);

        DonHang donHang = donHangRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn"));

        // check quyền
        if (!donHang.getNguoiDung().getId().equals(user.getId())) {
            throw new SecurityException("Không có quyền");
        }

        // chỉ cho hủy khi chờ xác nhận
        if (donHang.getTrangThai() != TrangThaiDonHang.CHO_XAC_NHAN) {
            throw new IllegalArgumentException("Không thể hủy");
        }

        // check 15 phút
        Duration duration = Duration.between(donHang.getNgayDat(), LocalDateTime.now());
        if (duration.toMinutes() > 15) {
            throw new IllegalArgumentException("Quá thời gian hủy");
        }

        // hoàn kho
        for (ChiTietDonHang ct : donHang.getChiTietDonHangs()) {
            Sach sach = ct.getSach();
            sach.setSoLuong(sach.getSoLuong() + ct.getSoLuong());
            sachRepository.save(sach);
        }

        donHang.setTrangThai(TrangThaiDonHang.DA_HUY);

        return mapToOrderDTO(donHangRepository.save(donHang));
    }
}
