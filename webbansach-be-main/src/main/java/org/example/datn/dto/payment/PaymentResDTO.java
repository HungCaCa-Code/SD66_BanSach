package org.example.datn.dto.payment;

import lombok.Data;

@Data
public class PaymentResDTO {
    private String status;
    private String message;
    private String url; // URL để chuyển hướng sang VNPAY
}