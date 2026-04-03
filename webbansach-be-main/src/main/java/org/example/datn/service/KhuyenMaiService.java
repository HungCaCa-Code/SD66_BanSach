package org.example.datn.service;

import org.example.datn.dto.KhuyenMaiDTO;
import org.example.datn.dto.PublicKhuyenMaiDTO;
import org.example.datn.entity.KhuyenMai;
import org.example.datn.exception.ResourceNotFoundException;
import org.example.datn.repository.KhuyenMaiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KhuyenMaiService {

    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;

    public Page<KhuyenMaiDTO> getAll(Pageable pageable) {
        return khuyenMaiRepository.findAll(pageable).map(this::mapToDTO);
    }

    public KhuyenMaiDTO create(KhuyenMaiDTO dto) {
        KhuyenMai km = mapToEntity(dto, new KhuyenMai());
        if (dto.getTrangThai() == null) {
            km.setTrangThai(true); // Mặc định
        }
        return mapToDTO(khuyenMaiRepository.save(km));
    }

    public KhuyenMaiDTO update(Integer id, KhuyenMaiDTO dto) {
        KhuyenMai km = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mã KM: " + id));
        km = mapToEntity(dto, km);
        return mapToDTO(khuyenMaiRepository.save(km));
    }

    public void delete(Integer id) {
        KhuyenMai km = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mã KM: " + id));
        khuyenMaiRepository.delete(km);
    }

    // --- Hàm Map ---
    private KhuyenMaiDTO mapToDTO(KhuyenMai km) {
        KhuyenMaiDTO dto = new KhuyenMaiDTO();
        dto.setId(km.getId());
        dto.setMa(km.getMa());
        dto.setMoTa(km.getMoTa());
        dto.setPhanTram(km.getPhanTram());
        dto.setNgayBatDau(km.getNgayBatDau());
        dto.setNgayKetThuc(km.getNgayKetThuc());
        dto.setTrangThai(km.getTrangThai()); // (Công tắc Bật/Tắt)

        // === LOGIC TÍNH TRẠNG THÁI ĐỘNG ===
        LocalDate homNay = LocalDate.now();

        if (Boolean.FALSE.equals(km.getTrangThai())) {
            dto.setTrangThaiHienTai("Đã tắt"); // Admin tự tắt
        } else if (km.getNgayBatDau().isAfter(homNay)) {
            dto.setTrangThaiHienTai("Sắp diễn ra"); // Chưa tới ngày
        } else if (km.getNgayKetThuc().isBefore(homNay)) {
            dto.setTrangThaiHienTai("Đã hết hạn"); // Đã qua ngày
        } else {
            dto.setTrangThaiHienTai("Đang diễn ra"); // Hợp lệ
        }
        // ===================================

        return dto;
    }

    private KhuyenMai mapToEntity(KhuyenMaiDTO dto, KhuyenMai km) {
        km.setMa(dto.getMa());
        km.setMoTa(dto.getMoTa());
        km.setPhanTram(dto.getPhanTram());
        km.setNgayBatDau(dto.getNgayBatDau());
        km.setNgayKetThuc(dto.getNgayKetThuc());
        km.setTrangThai(dto.getTrangThai());
        return km;
    }


    //Lấy tất cả mã KM còn hạn (cho ComboBox)
    public List<PublicKhuyenMaiDTO> getAvailablePromotions() {
        LocalDate homNay = LocalDate.now();
        List<KhuyenMai> list = khuyenMaiRepository
                .findAllByTrangThaiIsTrueAndNgayBatDauLessThanEqualAndNgayKetThucGreaterThanEqual(homNay, homNay);

        return list.stream().map(this::mapToPublicDTO).collect(Collectors.toList());
    }


    //Kiểm tra 1 mã KM cụ thể (Khi user nhập tay/tìm kiếm)
    public PublicKhuyenMaiDTO checkPromotion(String maCode) {
        LocalDate homNay = LocalDate.now();
        KhuyenMai km = khuyenMaiRepository
                .findByMaAndTrangThaiIsTrueAndNgayBatDauLessThanEqualAndNgayKetThucGreaterThanEqual(
                        maCode.trim().toUpperCase(), // Check mã (viết hoa, cắt khoảng trắng)
                        homNay,
                        homNay)
                .orElseThrow(() -> new ResourceNotFoundException("Mã khuyến mãi không hợp lệ hoặc đã hết hạn"));

        return mapToPublicDTO(km);
    }

    // --- Hàm Map DTO Công khai ---
    private PublicKhuyenMaiDTO mapToPublicDTO(KhuyenMai km) {
        PublicKhuyenMaiDTO dto = new PublicKhuyenMaiDTO();
        dto.setMa(km.getMa());
        dto.setMoTa(km.getMoTa());
        dto.setPhanTram(km.getPhanTram());
        return dto;
    }
}