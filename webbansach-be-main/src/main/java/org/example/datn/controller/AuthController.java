package org.example.datn.controller;

import jakarta.validation.Valid;
import org.example.datn.dto.request.LoginRequestDTO;
import org.example.datn.dto.request.RegisterRequestDTO;
import org.example.datn.dto.response.LoginResponseDTO;
import org.example.datn.entity.NguoiDung;
import org.example.datn.repository.NguoiDungRepository;
import org.example.datn.security.JwtUtil;
import org.example.datn.service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private NguoiDungService nguoiDungService;
    @Autowired

    private NguoiDungRepository nguoiDungRepository;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody LoginRequestDTO loginRequest) throws Exception {

        // Xác thực người dùng
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getTenDangNhap(), loginRequest.getMatKhau())
        );

        // Nếu xác thực thành công, tạo token
        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        final String jwt = jwtUtil.generateToken(userDetails);

        // Lấy vai trò (role)
        // Lấy đầy đủ thông tin NguoiDung Entity

        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(userDetails.getUsername())

                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy User sau khi xác thực"));
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        String clientRole = "user";

        if (role.equals("ROLE_QUAN_TRI")) {

            clientRole = "admin";

        }
        // Tạo Response DTO đầy đủ (Fix lỗi 1)
        LoginResponseDTO response = new LoginResponseDTO();

        response.setJwt(jwt);

        response.setTenDangNhap(nguoiDung.getTenDangNhap());

        response.setHoTen(nguoiDung.getHoTen());

        response.setEmail(nguoiDung.getEmail());

        response.setSoDienThoai(nguoiDung.getSoDienThoai());

        response.setDiaChi(nguoiDung.getDiaChi());

        response.setVaiTro(clientRole);

        response.setNgayTao(nguoiDung.getNgayTao());

        return ResponseEntity.ok(response);
    }

    // === THÊM ENDPOINT ĐĂNG KÝ ===
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        try {
            NguoiDung newUser = nguoiDungService.registerNewUser(registerRequest);

            // Tùy chọn: Tự động đăng nhập sau khi đăng ký
            final UserDetails userDetails = userDetailsService.loadUserByUsername(newUser.getTenDangNhap());
            final String jwt = jwtUtil.generateToken(userDetails);
            // Tạo Response DTO đầy đủ (Fix lỗi 2)

            LoginResponseDTO response = new LoginResponseDTO();

            response.setJwt(jwt);

            response.setTenDangNhap(newUser.getTenDangNhap());

            response.setHoTen(newUser.getHoTen());

            response.setEmail(newUser.getEmail());

            response.setSoDienThoai(newUser.getSoDienThoai());

            response.setDiaChi(newUser.getDiaChi());

            response.setVaiTro("user");

            response.setNgayTao(newUser.getNgayTao());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}