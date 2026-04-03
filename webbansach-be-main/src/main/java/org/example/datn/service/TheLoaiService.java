package org.example.datn.service;

import org.example.datn.dto.TheLoaiDTO;
import org.example.datn.entity.TheLoai;
import org.example.datn.exception.DuplicateResourceException;
import org.example.datn.exception.ResourceNotFoundException;
import org.example.datn.repository.TheLoaiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
@Service
public class TheLoaiService {

    @Autowired
    private TheLoaiRepository theLoaiRepository;

    public Page<TheLoaiDTO> getAllTheLoai(Pageable pageable, String keyword) {
        Specification<TheLoai> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.trim().toLowerCase() + "%";
                // Tìm theo Tên thể loại HOẶC Mô tả
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("tenTheLoai")), likePattern),
                        cb.like(cb.lower(root.get("moTa")), likePattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return theLoaiRepository.findAll(spec, pageable).map(this::mapToDTO);
    }

    public TheLoaiDTO getTheLoaiById(Integer id) {
        TheLoai theLoai = theLoaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại với ID: " + id));
        return mapToDTO(theLoai);
    }

    public TheLoaiDTO createTheLoai(TheLoaiDTO theLoaiDTO) {
        if (theLoaiRepository.existsByTenTheLoai(theLoaiDTO.getTenTheLoai().trim())) {
            throw new DuplicateResourceException("Tên thể loại đã tồn tại!");
        }
        TheLoai theLoai = mapToEntity(theLoaiDTO, new TheLoai());
        return mapToDTO(theLoaiRepository.save(theLoai));
    }

    public TheLoaiDTO updateTheLoai(Integer id, TheLoaiDTO theLoaiDTO) {
        TheLoai theLoai = theLoaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại với ID: " + id));

        if (theLoaiRepository.existsByTenTheLoaiAndIdNot(theLoaiDTO.getTenTheLoai().trim(), id)) {
            throw new DuplicateResourceException("Tên thể loại đã được sử dụng!");
        }
        theLoai = mapToEntity(theLoaiDTO, theLoai);
        return mapToDTO(theLoaiRepository.save(theLoai));
    }

    public void deleteTheLoai(Integer id) {
        TheLoai theLoai = theLoaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại với ID: " + id));
        theLoaiRepository.delete(theLoai);
    }

    private TheLoaiDTO mapToDTO(TheLoai theLoai) {
        TheLoaiDTO dto = new TheLoaiDTO();
        dto.setId(theLoai.getId());
        dto.setTenTheLoai(theLoai.getTenTheLoai());
        dto.setMoTa(theLoai.getMoTa());
        return dto;
    }

    private TheLoai mapToEntity(TheLoaiDTO dto, TheLoai theLoai) {
        theLoai.setTenTheLoai(dto.getTenTheLoai().trim());
        if (dto.getMoTa() != null) {
            theLoai.setMoTa(dto.getMoTa().trim());
        }
        return theLoai;
    }
}