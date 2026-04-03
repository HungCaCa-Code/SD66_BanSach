package org.example.datn.controller;

import org.example.datn.dto.ThongBaoDTO;
import org.example.datn.entity.ThongBao;
import org.example.datn.repository.NguoiDungRepository;
import org.example.datn.repository.ThongBaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("isAuthenticated()") // Ai đăng nhập cũng xem được
public class ThongBaoController {

    @Autowired
    private ThongBaoRepository thongBaoRepository;
    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @GetMapping("/me")
    public ResponseEntity<List<ThongBaoDTO>> getMyNotifications() { // <-- Đổi kiểu trả về
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = nguoiDungRepository.findByTenDangNhap(username).orElseThrow();

        List<ThongBao> thongBaos = thongBaoRepository.findByNguoiDungOrderByNgayTaoDesc(user);

        // Chuyển Entity sang DTO
        List<ThongBaoDTO> dtos = thongBaos.stream().map(tb -> {
            ThongBaoDTO dto = new ThongBaoDTO();
            dto.setId(tb.getId());
            dto.setNoiDung(tb.getNoiDung());
            dto.setNgayTao(tb.getNgayTao());
            dto.setDaDoc(tb.getDaDoc());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}