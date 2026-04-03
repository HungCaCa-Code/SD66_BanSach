package org.example.datn.service;

import org.example.datn.entity.NguoiDung;
import org.example.datn.entity.ThongBao;
import org.example.datn.repository.ThongBaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ThongBaoService {

    @Autowired
    private ThongBaoRepository thongBaoRepository;

    @Transactional
    public void sendNotification(NguoiDung user, String message) {
        ThongBao thongBao = new ThongBao();
        thongBao.setNguoiDung(user);
        thongBao.setNoiDung(message);
        thongBao.setNgayTao(LocalDateTime.now());
        thongBao.setDaDoc(false);

        thongBaoRepository.save(thongBao);
    }
}