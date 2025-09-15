package org.example.startapi.domain.repository;

import org.example.startapi.domain.entity.UserQna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserQnaRepository extends JpaRepository<UserQna, Long> {

    // 전화번호(phone)를 기준으로 모든 문의 내역을 찾는 메소드
    List<UserQna> findByPhone(String phone);

    // 전화번호(phone)와 등록 ID 를 기준으로 특정 문의 내역을 찾는 메소드 (삭제용)
    Optional<UserQna> findByIdAndPhone(int id, String phone);

    @Query("SELECT q FROM UserQna q LEFT JOIN FETCH q.replies")
    List<UserQna> findAllWithReplies();
}