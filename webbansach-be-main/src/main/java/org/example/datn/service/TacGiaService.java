package org.example.datn.service;

import org.example.datn.dto.TacGiaDTO;
import org.example.datn.entity.TacGia;
import org.example.datn.exception.DuplicateResourceException;
import org.example.datn.exception.ResourceNotFoundException;
import org.example.datn.repository.TacGiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

@Service
public class TacGiaService {

    @Autowired
    private TacGiaRepository tacGiaRepository;

    public Page<TacGiaDTO> getAllTacGias(Pageable pageable, String keyword) {
        Specification<TacGia> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Nếu có keyword, tìm theo Tên tác giả HOẶC Tiểu sử
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("tenTacGia")), likePattern),
                        cb.like(cb.lower(root.get("tieuSu")), likePattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return tacGiaRepository.findAll(spec, pageable).map(this::mapToDTO);
    }

    public TacGiaDTO getTacGiaById(Integer id) {
        TacGia tacGia = tacGiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả với ID: " + id));
        return mapToDTO(tacGia);
    }

    public TacGiaDTO createTacGia(TacGiaDTO tacGiaDTO) {
        if (tacGiaRepository.existsByTenTacGia(tacGiaDTO.getTenTacGia().trim())) {
            throw new DuplicateResourceException("Tên tác giả đã tồn tại!");
        }
        TacGia tacGia = mapToEntity(tacGiaDTO, new TacGia());
        return mapToDTO(tacGiaRepository.save(tacGia));
    }

    public TacGiaDTO updateTacGia(Integer id, TacGiaDTO tacGiaDTO) {
        TacGia tacGia = tacGiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả với ID: " + id));

        if (tacGiaRepository.existsByTenTacGiaAndIdNot(tacGiaDTO.getTenTacGia().trim(), id)) {
            throw new DuplicateResourceException("Tên tác giả đã được sử dụng!");
        }

        tacGia = mapToEntity(tacGiaDTO, tacGia);
        return mapToDTO(tacGiaRepository.save(tacGia));
    }

    public void deleteTacGia(Integer id) {
        TacGia tacGia = tacGiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả với ID: " + id));
        tacGiaRepository.delete(tacGia);
    }

    private TacGiaDTO mapToDTO(TacGia tacGia) {
        TacGiaDTO dto = new TacGiaDTO();
        dto.setId(tacGia.getId());
        dto.setTenTacGia(tacGia.getTenTacGia());
        dto.setTieuSu(tacGia.getTieuSu());
        dto.setNgaySinh(tacGia.getNgaySinh());
        return dto;
    }

    private TacGia mapToEntity(TacGiaDTO dto, TacGia tacGia) {
        tacGia.setTenTacGia(dto.getTenTacGia().trim());

        if (dto.getTieuSu() != null) {
            tacGia.setTieuSu(dto.getTieuSu().trim());
        }

        tacGia.setNgaySinh(dto.getNgaySinh());
        return tacGia;
    }
}