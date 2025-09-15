package org.example.startapi.domain.repository;

import co.elastic.clients.json.JsonpUtils;
import org.example.startapi.domain.entity.TodayBread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodayBreadRepository extends JpaRepository<TodayBread, Long> {

    @Query("select tb from TodayBread tb left join fetch tb.ratings")
    List<TodayBread> findAllWithRatings();
}
