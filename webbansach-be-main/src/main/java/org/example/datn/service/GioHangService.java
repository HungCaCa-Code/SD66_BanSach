package org.example.datn.service;

import org.example.datn.dto.cart.AddToCartRequestDTO;
import org.example.datn.dto.cart.CartItemDTO;
import org.example.datn.dto.cart.CartViewDTO;
import org.example.datn.entity.GioHang;
import org.example.datn.entity.NguoiDung;
import org.example.datn.entity.Sach;
import org.example.datn.exception.ResourceNotFoundException;
import org.example.datn.repository.GioHangRepository;
import org.example.datn.repository.NguoiDungRepository;
import org.example.datn.repository.SachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GioHangService {

    @Autowired
    private GioHangRepository gioHangRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private SachRepository sachRepository;

    // Lấy giỏ hàng của người dùng
    @Transactional(readOnly = true)
    public CartViewDTO getCart(String tenNguoiDung) {
        NguoiDung user = getUser(tenNguoiDung);
        List<GioHang> cartItems = gioHangRepository.findByNguoiDung(user);

        List<CartItemDTO> dtos = cartItems.stream()
                .map(this::mapToCartItemDTO)
                .collect(Collectors.toList());

        BigDecimal tongTien = dtos.stream()
                .map(CartItemDTO::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CartViewDTO cartView = new CartViewDTO();
        cartView.setItems(dtos);
        cartView.setTongTien(tongTien);
        return cartView;
    }

    // Thêm vào giỏ hàng
    @Transactional
    public CartItemDTO addToCart(String tenNguoiDung, AddToCartRequestDTO request) {
        NguoiDung user = getUser(tenNguoiDung);
        Sach sach = sachRepository.findById(request.getSachId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));

        // === CHECK TỒN KHO KHI THÊM MỚI ===
        if (request.getSoLuong() > sach.getSoLuong()) {
            throw new IllegalArgumentException("Số lượng yêu cầu vượt quá tồn kho (Còn lại: " + sach.getSoLuong() + ")");
        }

        Optional<GioHang> existingItemOpt = gioHangRepository.findByNguoiDung(user).stream()
                .filter(item -> item.getSach().getId().equals(sach.getId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            GioHang existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getSoLuong() + request.getSoLuong();

            // === CHECK TỒN KHO KHI CỘNG DỒN ===
            if (newQuantity > sach.getSoLuong()) {
                throw new IllegalArgumentException("Tổng số lượng trong giỏ vượt quá tồn kho (Còn lại: " + sach.getSoLuong() + ")");
            }

            existingItem.setSoLuong(newQuantity);
            return mapToCartItemDTO(gioHangRepository.save(existingItem));
        } else {
            // Nếu CHƯA -> Tạo dòng mới
            GioHang newItem = new GioHang();
            newItem.setNguoiDung(user);
            newItem.setSach(sach);
            newItem.setSoLuong(request.getSoLuong());
            newItem.setNgayThem(LocalDateTime.now());
            return mapToCartItemDTO(gioHangRepository.save(newItem));
        }
    }

    // Xóa khỏi giỏ hàng
    @Transactional
    public void removeFromCart(String tenNguoiDung, Integer idGioHang) {
        NguoiDung user = getUser(tenNguoiDung);
        GioHang cartItem = gioHangRepository.findById(idGioHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mục trong giỏ hàng"));

        // Đảm bảo người dùng chỉ xóa được mục trong giỏ của chính mình
        if (!cartItem.getNguoiDung().getId().equals(user.getId())) {
            throw new SecurityException("Không có quyền xóa mục này");
        }

        gioHangRepository.delete(cartItem);
    }

    // Lấy user (hàm tiện ích)
    private NguoiDung getUser(String tenNguoiDung) {
        return nguoiDungRepository.findByTenDangNhap(tenNguoiDung)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    // Map Entity sang DTO (hàm tiện ích)
    private CartItemDTO mapToCartItemDTO(GioHang item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setIdGioHang(item.getId());
        dto.setSachId(item.getSach().getId());
        dto.setTenSach(item.getSach().getTieuDe());
        dto.setHinhAnh(item.getSach().getHinhAnh());
        dto.setGia(item.getSach().getGia());
        dto.setSoLuong(item.getSoLuong());
        dto.setThanhTien(item.getSach().getGia().multiply(BigDecimal.valueOf(item.getSoLuong())));
        return dto;
    }

    @Transactional
    public CartItemDTO updateCartItem(String tenNguoiDung, Integer idGioHang, Integer soLuongMoi) {
        NguoiDung user = getUser(tenNguoiDung);
        GioHang cartItem = gioHangRepository.findById(idGioHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mục giỏ hàng này"));

        // Bảo mật: Check xem mục này có đúng là của user đang đăng nhập không
        if (!cartItem.getNguoiDung().getId().equals(user.getId())) {
            throw new SecurityException("Bạn không có quyền sửa mục giỏ hàng của người khác");
        }

        if (soLuongMoi <= 0) {
            gioHangRepository.delete(cartItem);
            return null;
        }

        // === CHECK TỒN KHO KHI CẬP NHẬT ===
        Sach sach = cartItem.getSach();
        if (soLuongMoi > sach.getSoLuong()) {
            throw new IllegalArgumentException("Số lượng yêu cầu vượt quá tồn kho (Còn lại: " + sach.getSoLuong() + ")");
        }
        // ==================================

        cartItem.setSoLuong(soLuongMoi);
        return mapToCartItemDTO(gioHangRepository.save(cartItem));
    }

    // 4. Tăng số lượng (+1)
    @Transactional
    public CartItemDTO increaseQuantity(String tenNguoiDung, Integer idGioHang) {
        NguoiDung user = getUser(tenNguoiDung);
        GioHang cartItem = gioHangRepository.findById(idGioHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mục giỏ hàng"));

        if (!cartItem.getNguoiDung().getId().equals(user.getId())) {
            throw new SecurityException("Không có quyền");
        }

        // Kiểm tra tồn kho
        if (cartItem.getSach().getSoLuong() < (cartItem.getSoLuong() + 1)) {
            throw new IllegalArgumentException("Không đủ hàng để tăng thêm");
        }

        cartItem.setSoLuong(cartItem.getSoLuong() + 1);
        return mapToCartItemDTO(gioHangRepository.save(cartItem));
    }

    // 5. Giảm số lượng (-1)
    @Transactional
    public CartItemDTO decreaseQuantity(String tenNguoiDung, Integer idGioHang) {
        NguoiDung user = getUser(tenNguoiDung);
        GioHang cartItem = gioHangRepository.findById(idGioHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mục giỏ hàng"));

        if (!cartItem.getNguoiDung().getId().equals(user.getId())) {
            throw new SecurityException("Không có quyền");
        }

        int newQuantity = cartItem.getSoLuong() - 1;
        if (newQuantity <= 0) {
            // Nếu giảm về 0 thì xóa luôn
            gioHangRepository.delete(cartItem);
            return null;
        }

        cartItem.setSoLuong(newQuantity);
        return mapToCartItemDTO(gioHangRepository.save(cartItem));
    }


}