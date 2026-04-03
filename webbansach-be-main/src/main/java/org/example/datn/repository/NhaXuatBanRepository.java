package org.example.datn.repository;

import org.example.datn.entity.NhaXuatBan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NhaXuatBanRepository extends JpaRepository<NhaXuatBan, Integer>, JpaSpecificationExecutor<NhaXuatBan> {

    // Kiểm tra tồn tại theo Tên nhà xuất bản
    boolean existsByTenNxb(String tenNxb);
    boolean existsByTenNxbAndIdNot(String tenNxb, Integer id);
}
