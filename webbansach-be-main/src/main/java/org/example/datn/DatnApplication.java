package org.example.datn;

import org.example.datn.entity.NguoiDung;
import org.example.datn.repository.NguoiDungRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DatnApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatnApplication.class, args);
        System.out.println("Application started successfully!");
    }

    // === THÊM BEAN NÀY ĐỂ HASH MẬT KHẨU ADMIN ===
    @Bean
    CommandLineRunner initDatabase(NguoiDungRepository nguoiDungRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Tìm admin
            NguoiDung admin = nguoiDungRepository.findByTenDangNhap("admin").orElse(null);

            if (admin != null && !admin.getMatKhau().startsWith("$2a$")) { // Kiểm tra nếu mk chưa được hash
                System.out.println("Hashing password cho admin...");
                admin.setMatKhau(passwordEncoder.encode("123456"));
                nguoiDungRepository.save(admin);
                System.out.println("Password của admin đã được hash.");
            }
        };
    }
}
