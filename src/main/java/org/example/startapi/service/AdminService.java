package org.example.startapi.service;

import lombok.RequiredArgsConstructor;
import org.example.startapi.domain.entity.*;
import org.example.startapi.domain.repository.*;
import org.example.startapi.dto.AdminDTO;
import org.example.startapi.dto.OrderDTO;
import org.example.startapi.dto.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 🌟 1. Spring의 Transactional을 사용하도록 수정

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 🌟 2. 클래스 레벨에 읽기 전용 트랜잭션 적용 (조회 성능 최적화)
public class AdminService {

    private final UserQnaRepository userQnaRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;
    private final AdminRepository adminRepository;
    private final OrderRepository orderRepository;

    // 관리자 로그인 메서드
    public Admin adminLogin(String adminId, String adminPassword) {
        return adminRepository.findByAdminIdAndAdminPassword(adminId, adminPassword).orElse(null);
    }

    // 모든 User 목록을 데이터베이스에서 조회하는 메소드 추가
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 사용자 정보를 업데이트하는 서비스 메소드 추가
    @Transactional // 🌟 3. 쓰기 작업이므로 readOnly=false로 오버라이드
    public User updateUser(String userId, UserDTO userDTO) {
        // 1. ID로 기존 사용자 정보를 DB에서 조회
        User existingUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // 2. DTO에 담겨온 정보로 기존 사용자 정보(엔티티)를 업데이트
        existingUser.setUserName(userDTO.getUserName());
        existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        existingUser.setUserAddress(userDTO.getUserAddress());
        existingUser.setUserPoint(userDTO.getUserPoint());
        existingUser.setPinNo(userDTO.getPinNo());

        // 🌟 5. @Transactional에 의해 자동 변경 감지(Dirty Checking)가 동작하므로 save 호출 불필요
        return existingUser;
    }
    // 모든 QnA 목록을 데이터베이스에서 조회하는 메소드
    public List<UserQna> getAllQna() {
        return userQnaRepository.findAllWithReplies();
    }

    // QnA에 답글을 등록
    @Transactional // 쓰기 작업이므로 readOnly=false로 오버라이드
    public boolean addReplyToQna(Long qnaId, String replyContent) {
        // ID로 문의(QnA) 엔티티를 찾습니다.
        return userQnaRepository.findById(qnaId).map(qna -> {
            // 1. 새로운 Reply 객체를 생성합니다. (내용과 부모 QnA를 전달)
            Reply newReply = new Reply(replyContent, qna);
            // 2. 생성된 Reply를 replies 테이블에 저장합니다.
            replyRepository.save(newReply);
            return true; // 성공
        }).orElse(false); // 해당 ID의 QnA를 찾지 못하면 false 반환
    }

    public List<Order> getAllOrderList() {
        List<Order> allOrders = orderRepository.findAllWithDetails();
        return allOrders;
    }

 }

