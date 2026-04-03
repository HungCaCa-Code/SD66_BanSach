package org.example.datn.service;

import org.example.datn.dto.SachDTO;
import org.example.datn.entity.NhaXuatBan;
import org.example.datn.entity.Sach;
import org.example.datn.entity.TacGia;
import org.example.datn.entity.TheLoai;
import org.example.datn.exception.DuplicateResourceException;
import org.example.datn.exception.ResourceNotFoundException;
import org.example.datn.repository.NhaXuatBanRepository;
import org.example.datn.repository.SachRepository;
import org.example.datn.repository.TacGiaRepository;
import org.example.datn.repository.TheLoaiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

@Service
public class SachService {

    @Autowired
    private SachRepository sachRepository;
    @Autowired
    private TacGiaRepository tacGiaRepository;
    @Autowired
    private NhaXuatBanRepository nhaXuatBanRepository;
    @Autowired
    private TheLoaiRepository theLoaiRepository;

    @Autowired
     private ImageUploadService imageUploadService;

    // Create (ĐÃ QUAY LẠI MULTIPART)
    public SachDTO createSach(SachDTO sachDTO, MultipartFile file) {

        // 0. Check trùng Combo (Tiêu đề + Tác giả + NXB)
        if (sachRepository.existsByTieuDeAndTacGiaIdAndNhaXuatBanId(
                sachDTO.getTieuDe().trim(),
                sachDTO.getIdTacGia(),
                sachDTO.getIdNxb())) {
            throw new DuplicateResourceException("Cuốn sách này (cùng Tên, Tác giả, NXB) đã tồn tại!");
        }
        // 1. Thêm lại logic upload ảnh
        String imageUrl = imageUploadService.uploadFile(file);
        sachDTO.setHinhAnh(imageUrl);

        // 2. Phần còn lại giữ nguyên
        Sach sach = new Sach();
        mapToEntity(sachDTO, sach);
        sach.setNgayTao(LocalDateTime.now());
        Sach savedSach = sachRepository.save(sach);
        return mapToDTO(savedSach);
    }

    // Read (Get by ID) (Giữ nguyên)
    public SachDTO getSachById(Integer id) {
        Sach sach = sachRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + id));
        return mapToDTO(sach);
    }

    // Read
    public Page<SachDTO> getAllSach(
            Pageable pageable,
            String sortBy,
            // Thêm các tham số lọc mới
            Integer theLoaiId,
            Integer tacGiaId,
            Integer nxbId,
            BigDecimal giaTu,
            BigDecimal giaDen,
            String keyword
    ) {

        // --- Xử lý SortBy (Giữ nguyên logic cũ) ---
        if (!pageable.getSort().isSorted() && (sortBy == null || sortBy.isEmpty() || sortBy.equals("newest"))) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("ngayTao").descending());
        }

        // --- Xử lý Filter (Logic mới) ---
        // (root, query, cb) là 3 thành phần để xây dựng query
        Specification<Sach> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // === 0. TÌM KIẾM CHUNG (KEYWORD) ===
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.trim().toLowerCase() + "%";

                // Tìm trong 4 trường: Tiêu đề Sách, Tên Tác giả, Tên NXB, Tên Thể loại
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("tieuDe")), likePattern),
                        cb.like(cb.lower(root.get("tacGia").get("tenTacGia")), likePattern),
                        cb.like(cb.lower(root.get("nhaXuatBan").get("tenNxb")), likePattern),
                        cb.like(cb.lower(root.get("theLoai").get("tenTheLoai")), likePattern)
                ));
            }

            // 1. Lọc theo thể loại (theLoaiId)
            if (theLoaiId != null) {
                predicates.add(cb.equal(root.get("theLoai").get("id"), theLoaiId));
            }
            // 2. Lọc theo tác giả (tacGiaId)
            if (tacGiaId != null) {
                predicates.add(cb.equal(root.get("tacGia").get("id"), tacGiaId));
            }
            // 3. Lọc theo NXB (nxbId)
            if (nxbId != null) {
                predicates.add(cb.equal(root.get("nhaXuatBan").get("id"), nxbId));
            }
            // 4. Lọc theo khoảng giá (giaTu, giaDen)
            if (giaTu != null && giaDen != null) {
                predicates.add(cb.between(root.get("gia"), giaTu, giaDen));
            } else if (giaTu != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("gia"), giaTu));
            } else if (giaDen != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("gia"), giaDen));
            }

            // Kết hợp tất cả điều kiện lọc bằng AND
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // --- Thực thi Query ---
        Page<Sach> sachPage;

        if (sortBy != null) {
            switch (sortBy) {
                case "best_selling":
                    // (Lưu ý: JPA Spec không hỗ trợ GROUP BY phức tạp,
                    // nên khi lọc "best_selling" ta sẽ bỏ qua các filter khác)
                    sachPage = sachRepository.findBestSelling(pageable);
                    break;

                case "highest_rated":
                    // (Tương tự, bỏ qua filter khác khi sort)
                    sachPage = sachRepository.findHighestRated(pageable);
                    break;

                case "most_favorited":
                    sachPage = sachRepository.findMostFavorited(pageable);
                    break;

                default:
                    // Mặc định (newest, price_asc, price_desc...):
                    // Chạy query CÓ LỌC (spec)
                    sachPage = sachRepository.findAll(spec, pageable);
                    break;
            }
        } else {
            // Chạy query CÓ LỌC (spec)
            sachPage = sachRepository.findAll(spec, pageable);
        }

        return sachPage.map(this::mapToDTO);
    }

    // Update (ĐÃ QUAY LẠI MULTIPART)
    public SachDTO updateSach(Integer id, SachDTO sachDTO, MultipartFile file) { // <-- Thêm lại file
        Sach sach = sachRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + id));

        // 1. Check trùng Combo (loại trừ chính nó)
        if (sachRepository.existsByTieuDeAndTacGiaIdAndNhaXuatBanIdAndIdNot(
                sachDTO.getTieuDe().trim(),
                sachDTO.getIdTacGia(),
                sachDTO.getIdNxb(),
                id)) {
            throw new DuplicateResourceException("Thông tin mới bị trùng với một cuốn sách khác đã có!");
        }

        // 1. Thêm lại logic kiểm tra file mới
        if (file != null && !file.isEmpty()) {
            String imageUrl = imageUploadService.uploadFile(file);
            sachDTO.setHinhAnh(imageUrl);
        }
        // Nếu không có file mới, mapToEntity sẽ dùng URL cũ từ DTO

        // 2. Phần còn lại giữ nguyên
        mapToEntity(sachDTO, sach);
        Sach updatedSach = sachRepository.save(sach);
        return mapToDTO(updatedSach);
    }

    // Delete
    public void deleteSach(Integer id) {
        Sach sach = sachRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + id));
        sachRepository.delete(sach);
    }

    // Map Entity to DTO
    private SachDTO mapToDTO(Sach sach) {
        SachDTO sachDTO = new SachDTO();
        sachDTO.setIdSach(sach.getId());
        sachDTO.setTieuDe(sach.getTieuDe());
        sachDTO.setIdTacGia(sach.getTacGia() != null ? sach.getTacGia().getId() : null);
        sachDTO.setTenTacGia(sach.getTacGia() != null ? sach.getTacGia().getTenTacGia() : null);
        sachDTO.setIdNxb(sach.getNhaXuatBan() != null ? sach.getNhaXuatBan().getId() : null);
        sachDTO.setTenNxb(sach.getNhaXuatBan() != null ? sach.getNhaXuatBan().getTenNxb() : null);
        sachDTO.setNamXuatBan(sach.getNamXuatBan());
        sachDTO.setGia(sach.getGia());
        sachDTO.setSoLuong(sach.getSoLuong());
        sachDTO.setMoTa(sach.getMoTa());
        sachDTO.setHinhAnh(sach.getHinhAnh());
        sachDTO.setIdTheLoai(sach.getTheLoai() != null ? sach.getTheLoai().getId() : null);
        sachDTO.setTenTheLoai(sach.getTheLoai() != null ? sach.getTheLoai().getTenTheLoai() : null);
        sachDTO.setNgayTao(sach.getNgayTao());
        return sachDTO;
    }

    // Map DTO to Entity (Giữ nguyên)
    private void mapToEntity(SachDTO sachDTO, Sach sach) {
        sach.setTieuDe(sachDTO.getTieuDe().trim());

        if (sachDTO.getIdTacGia() != null) {
            TacGia tacGia = tacGiaRepository.findById(sachDTO.getIdTacGia())
                    .orElseThrow(() -> new ResourceNotFoundException("Tác giả không tồn tại"));
            sach.setTacGia(tacGia);
        }
        if (sachDTO.getIdNxb() != null) {
            NhaXuatBan nhaXuatBan = nhaXuatBanRepository.findById(sachDTO.getIdNxb())
                    .orElseThrow(() -> new ResourceNotFoundException("Nhà xuất bản không tồn tại"));
            sach.setNhaXuatBan(nhaXuatBan);
        }
        if (sachDTO.getIdTheLoai() != null) {
            TheLoai theLoai = theLoaiRepository.findById(sachDTO.getIdTheLoai())
                    .orElseThrow(() -> new ResourceNotFoundException("Thể loại không tồn tại"));
            sach.setTheLoai(theLoai);
        }

        if (sachDTO.getMoTa() != null) {
            sach.setMoTa(sachDTO.getMoTa().trim());
        }

        sach.setNamXuatBan(sachDTO.getNamXuatBan());
        sach.setGia(sachDTO.getGia());
        sach.setSoLuong(sachDTO.getSoLuong());
        sach.setMoTa(sachDTO.getMoTa());
        sach.setHinhAnh(sachDTO.getHinhAnh());
    }
}