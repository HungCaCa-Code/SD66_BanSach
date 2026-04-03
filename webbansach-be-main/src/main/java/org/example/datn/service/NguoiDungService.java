package org.example.datn.service;

import org.example.datn.dto.NguoiDungDTO;

import org.example.datn.dto.UserProfileUpdateDTO;
import org.example.datn.dto.request.RegisterRequestDTO;
import org.example.datn.entity.NguoiDung;
import org.example.datn.exception.ResourceNotFoundException;
import org.example.datn.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NguoiDungService {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Dùng cho Đăng ký
    @Transactional
    public NguoiDung registerNewUser(RegisterRequestDTO registerRequest) {
        // Kiểm tra
        if (nguoiDungRepository.findByTenDangNhap(registerRequest.getTenDangNhap()).isPresent()) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }
        if (nguoiDungRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        NguoiDung newUser = new NguoiDung();
        newUser.setTenDangNhap(registerRequest.getTenDangNhap());
        newUser.setMatKhau(passwordEncoder.encode(registerRequest.getMatKhau())); // Băm mật khẩu
        newUser.setHoTen(registerRequest.getHoTen());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setVaiTro("KHACH_HANG"); // Mặc định là khách hàng
        newUser.setNgayTao(LocalDateTime.now()); // Sử dụng LocalDateTime

        return nguoiDungRepository.save(newUser);
    }

    // 2. Dùng cho "My Profile"
    public NguoiDungDTO getUserProfile(String tenDangNhap) {
        NguoiDung user = nguoiDungRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        return mapToDTO(user);
    }

    // 3. Dùng cho Cập nhật "My Profile"
    @Transactional
    public NguoiDungDTO updateUserProfile(String tenDangNhap, UserProfileUpdateDTO profileUpdate) {
        NguoiDung user = nguoiDungRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        // Cập nhật các trường cho phép
        user.setHoTen(profileUpdate.getHoTen());
        user.setEmail(profileUpdate.getEmail());
        user.setSoDienThoai(profileUpdate.getSoDienThoai());
        user.setDiaChi(profileUpdate.getDiaChi());

        NguoiDung updatedUser = nguoiDungRepository.save(user);
        return mapToDTO(updatedUser);
    }

    // Hàm chuyển đổi Entity sang DTO an toàn
    private NguoiDungDTO mapToDTO(NguoiDung user) {
        NguoiDungDTO dto = new NguoiDungDTO();
        dto.setId(user.getId());
        dto.setTenDangNhap(user.getTenDangNhap());
        dto.setHoTen(user.getHoTen());
        dto.setEmail(user.getEmail());
        dto.setSoDienThoai(user.getSoDienThoai());
        dto.setDiaChi(user.getDiaChi());
        dto.setVaiTro(user.getVaiTro());
        dto.setNgayTao(user.getNgayTao());
        return dto;
    }

    // 4. (Admin) Lấy tất cả người dùng (phân trang)
    public Page<NguoiDungDTO> getAllUsers(Pageable pageable) {
        Page<NguoiDung> users = nguoiDungRepository.findAll(pageable);
        return users.map(this::mapToDTO); // Dùng lại hàm mapToDTO an toàn
    }

    // 5. (Admin) Cập nhật vai trò
    @Transactional
    public NguoiDungDTO updateUserRole(Integer userId, String newRole) {
        NguoiDung user = nguoiDungRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        // Kiểm tra xem vai trò có hợp lệ không
        if (!newRole.equals("KHACH_HANG") && !newRole.equals("QUAN_TRI")) {
            throw new IllegalArgumentException("Vai trò không hợp lệ. Chỉ chấp nhận KHACH_HANG hoặc QUAN_TRI.");
        }

        user.setVaiTro(newRole); //
        NguoiDung updatedUser = nguoiDungRepository.save(user);
        return mapToDTO(updatedUser);
    }
}