package org.example.datn.service;

import org.example.datn.dto.wishlist.WishlistItemDTO;
import org.example.datn.entity.NguoiDung;
import org.example.datn.entity.Sach;
import org.example.datn.entity.YeuThich;
import org.example.datn.exception.ResourceNotFoundException;
import org.example.datn.repository.NguoiDungRepository;
import org.example.datn.repository.SachRepository;
import org.example.datn.repository.YeuThichRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class YeuThichService {

    @Autowired
    private YeuThichRepository yeuThichRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private SachRepository sachRepository;

    // 1. Lấy danh sách yêu thích của tôi
    @Transactional(readOnly = true)
    public List<WishlistItemDTO> getMyWishlist(String tenNguoiDung) {
        NguoiDung user = getUser(tenNguoiDung);
        List<YeuThich> items = yeuThichRepository.findByNguoiDung(user);
        return items.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // 2. Thêm/Xóa (Toggle) một mục yêu thích
    @Transactional
    public boolean toggleWishlistItem(String tenNguoiDung, Integer sachId) {
        NguoiDung user = getUser(tenNguoiDung);
        Sach sach = sachRepository.findById(sachId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));

        Optional<YeuThich> existingItem = yeuThichRepository.findByNguoiDungAndSach(user, sach);

        if (existingItem.isPresent()) {
            // Nếu đã thích -> Xóa (Bỏ thích)
            yeuThichRepository.delete(existingItem.get());
            return false; // Trả về false (đã bỏ thích)
        } else {
            // Nếu chưa thích -> Thêm
            YeuThich newItem = new YeuThich();
            newItem.setNguoiDung(user);
            newItem.setSach(sach);
            newItem.setNgayThem(LocalDateTime.now());
            yeuThichRepository.save(newItem);
            return true; // Trả về true (đã thêm)
        }
    }

    // Hàm tiện ích
    private NguoiDung getUser(String tenNguoiDung) {
        return nguoiDungRepository.findByTenDangNhap(tenNguoiDung)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    // Hàm tiện ích
    private WishlistItemDTO mapToDTO(YeuThich item) {
        WishlistItemDTO dto = new WishlistItemDTO();
        dto.setIdYeuThich(item.getId());
        dto.setSachId(item.getSach().getId());
        dto.setTenSach(item.getSach().getTieuDe());
        dto.setHinhAnh(item.getSach().getHinhAnh());
        dto.setGia(item.getSach().getGia());
        return dto;
    }
}