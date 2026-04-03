package org.example.datn.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageUploadService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "datn_bansach" // Tên thư mục trên Cloudinary
            ));
            // Trả về URL an toàn (https) của ảnh
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Không thể upload ảnh", e);
        }
    }
}