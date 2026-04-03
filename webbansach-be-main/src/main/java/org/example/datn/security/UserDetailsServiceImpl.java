package org.example.datn.security;

import org.example.datn.entity.NguoiDung; // Sửa tên file này nếu sai
import org.example.datn.repository.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Override
    public UserDetails loadUserByUsername(String tenDangNhap) throws UsernameNotFoundException {
        // Các lỗi "cannot find symbol" ở đây sẽ được khắc phục ở Bước 3
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Không tìm thấy người dùng: " + tenDangNhap));

        // === SỬA LỖI LOGIC TẠI ĐÂY ===
        // Code cũ: new SimpleGrantedAuthority("ROLE_" + nguoiDung.getVaiTro().name())
        // Code mới (vì vaiTro là String):
        Set<GrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + nguoiDung.getVaiTro())
        );

        return new User(nguoiDung.getTenDangNhap(), nguoiDung.getMatKhau(), authorities);
    }
}