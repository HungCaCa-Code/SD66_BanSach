package org.example.datn.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderUpdateStatusRequestDTO {

    @NotBlank(message = "Trạng thái không được để trống")
    // Sẽ nhận giá trị là: "DA_XAC_NHAN", "DANG_GIAO", "HOAN_THANH", "DA_HUY"
    private String trangThai;
}