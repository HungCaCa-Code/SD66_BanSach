package org.example.datn.repository;

import org.example.datn.entity.TheLoai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TheLoaiRepository extends JpaRepository<TheLoai, Integer>, JpaSpecificationExecutor<TheLoai> {

    // Kiểm tra tồn tại theo Tên thể loại
    boolean existsByTenTheLoai(String tenTheLoai);
    boolean existsByTenTheLoaiAndIdNot(String tenTheLoai, Integer id);
}
