package org.example.datn.repository;

import org.example.datn.entity.TacGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TacGiaRepository extends JpaRepository<TacGia, Integer>, JpaSpecificationExecutor<TacGia> {

    // Kiểm tra tồn tại theo Tên tác giả
    boolean existsByTenTacGia(String tenTacGia);
    boolean existsByTenTacGiaAndIdNot(String tenTacGia, Integer id);
}
