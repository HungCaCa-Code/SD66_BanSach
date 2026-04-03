package org.example.datn.service;

import org.example.datn.dto.NhaXuatBanDTO;
import org.example.datn.entity.NhaXuatBan;
import org.example.datn.exception.DuplicateResourceException;
import org.example.datn.exception.ResourceNotFoundException;
import org.example.datn.repository.NhaXuatBanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

@Service
public class NhaXuatBanService {

    @Autowired
    private NhaXuatBanRepository nhaXuatBanRepository;

    public Page<NhaXuatBanDTO> getAllNxb(Pageable pageable, String keyword) {
        Specification<NhaXuatBan> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.trim().toLowerCase() + "%";
                // Tìm theo Tên NXB, Địa chỉ, hoặc Số điện thoại
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("tenNxb")), likePattern),
                        cb.like(cb.lower(root.get("diaChi")), likePattern),
                        cb.like(cb.lower(root.get("soDienThoai")), likePattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return nhaXuatBanRepository.findAll(spec, pageable).map(this::mapToDTO);
    }

    public NhaXuatBanDTO getNxbById(Integer id) {
        NhaXuatBan nxb = nhaXuatBanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy NXB với ID: " + id));
        return mapToDTO(nxb);
    }

    public NhaXuatBanDTO createNxb(NhaXuatBanDTO nxbDTO) {

        if (nhaXuatBanRepository.existsByTenNxb(nxbDTO.getTenNxb().trim())) {
            throw new DuplicateResourceException("Tên NXB đã tồn tại!");
        }
        NhaXuatBan nxb = mapToEntity(nxbDTO, new NhaXuatBan());
        return mapToDTO(nhaXuatBanRepository.save(nxb));
    }

    public NhaXuatBanDTO updateNxb(Integer id, NhaXuatBanDTO nxbDTO) {
        NhaXuatBan nxb = nhaXuatBanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy NXB với ID: " + id));

        if (nhaXuatBanRepository.existsByTenNxbAndIdNot(nxbDTO.getTenNxb().trim(), id)) {
            throw new DuplicateResourceException("Tên NXB đã được sử dụng!");
        }
        nxb = mapToEntity(nxbDTO, nxb);
        return mapToDTO(nhaXuatBanRepository.save(nxb));
    }

    public void deleteNxb(Integer id) {
        NhaXuatBan nxb = nhaXuatBanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy NXB với ID: " + id));
        nhaXuatBanRepository.delete(nxb);
    }

    private NhaXuatBanDTO mapToDTO(NhaXuatBan nxb) {
        NhaXuatBanDTO dto = new NhaXuatBanDTO();
        dto.setId(nxb.getId());
        dto.setTenNxb(nxb.getTenNxb());
        dto.setDiaChi(nxb.getDiaChi());
        dto.setSoDienThoai(nxb.getSoDienThoai());
        return dto;
    }

    private NhaXuatBan mapToEntity(NhaXuatBanDTO dto, NhaXuatBan nxb) {
        nxb.setTenNxb(dto.getTenNxb().trim());
        if (dto.getDiaChi() != null) {
            nxb.setDiaChi(dto.getDiaChi().trim());
        }
        if (dto.getSoDienThoai() != null) {
            nxb.setSoDienThoai(dto.getSoDienThoai().trim());
        }
        return nxb;
    }
}