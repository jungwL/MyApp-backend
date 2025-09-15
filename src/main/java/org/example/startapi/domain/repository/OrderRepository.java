package org.example.startapi.domain.repository;

import org.example.startapi.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * N+1 문제를 해결하기 위해 fetch join을 사용하여 Order와 연관된
     * User, TodayBread 엔티티를 한 번의 쿼리로 함께 조회합니다.
     * @return 연관 엔티티 정보가 포함된 모든 주문 목록
     */
    @Query("SELECT o FROM Order o JOIN FETCH o.user JOIN FETCH o.todayBread")
    List<Order> findAllWithDetails();
}
