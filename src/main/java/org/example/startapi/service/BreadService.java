package org.example.startapi.service;

import lombok.RequiredArgsConstructor;
import org.example.startapi.domain.entity.TodayBread;
import org.example.startapi.domain.repository.TodayBreadRepository;
import org.example.startapi.dto.TodayBreadDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회 기능이므로 readOnly = true 설정
public class BreadService {

    // final 키워드를 통해 생성자 주입이 되도록 설정
    private final TodayBreadRepository todayBreadRepository;

    /**
     * @Cacheable: 이 메소드의 결과는 'todayBreads'라는 캐시에 저장됩니다.
     * 동일한 요청이 다시 오면, 메소드 내부 코드를 실행하지 않고 Redis에서 즉시 결과를 반환합니다.
     */
    @Cacheable(value = "todayBreads")
    public Map<String, List<TodayBreadDTO>> getTodayBreadsCategorized() {
        // 이 로그는 DB를 실제로 조회할 때만 (즉, 캐시에 데이터가 없을 때만) 찍힙니다.
        System.out.println("--- 처음 DB에서 빵 목록을 조회합니다 (캐시 없음) ---");
        List<TodayBread> allBreads = todayBreadRepository.findAllWithRatings();
        List<TodayBreadDTO> dtoList = allBreads.stream()
                .map(this::convertToTodayBreadDto)
                .collect(Collectors.toList());
        return dtoList.stream()
                .collect(Collectors.groupingBy(TodayBreadDTO::getCategory));
    }

    /**
     * @CacheEvict: 상품이 삭제되면 'todayBreads' 캐시의 모든 데이터를 삭제합니다.
     * 데이터의 일관성을 위해 캐시를 비워, 다음 조회 시 최신 정보를 DB에서 가져오게 합니다.
     */
    @Transactional
    @CacheEvict(value = "todayBreads", allEntries = true)
    public boolean delTodayBreadList(Long breadId) {
        System.out.println("--- 상품 삭제! 'todayBreads' 캐시를 비웁니다. ---");
        Optional<TodayBread> breadOptional = todayBreadRepository.findById(breadId);
        if (breadOptional.isPresent()) {
            todayBreadRepository.delete(breadOptional.get());
            return true;
        }
        return false;
    }

    /**
     * @CacheEvict: 새로운 상품이 추가되면 'todayBreads' 캐시의 모든 데이터를 삭제합니다.
     */
    @Transactional
    @CacheEvict(value = "todayBreads", allEntries = true)
    public TodayBreadDTO subTodayBread(TodayBreadDTO requsetTodayBreadDTO) {
        System.out.println("--- 신규 상품 추가! 'todayBreads' 캐시비우기. ---");
        TodayBread newTodayBread = new TodayBread();
        // TodayBread 엔티티에 Setter가 정의되어 있어야 합니다.
        newTodayBread.setCategory(requsetTodayBreadDTO.getCategory());
        newTodayBread.setName(requsetTodayBreadDTO.getName());
        newTodayBread.setPrice(requsetTodayBreadDTO.getPrice());
        newTodayBread.setIngredients(requsetTodayBreadDTO.getIngredients());
        newTodayBread.setImage(requsetTodayBreadDTO.getImage());
        return convertToTodayBreadDto(todayBreadRepository.save(newTodayBread)); // spring jpa Insert문
    }

    // Entity를 DTO로 변환하는 헬퍼 메소드
    private TodayBreadDTO convertToTodayBreadDto(TodayBread bread) {
        double avgRating = bread.getRatings().stream()
                .mapToDouble(rating -> rating.getRating())
                .average()
                .orElse(0.0);

        return TodayBreadDTO.builder()
                .id(bread.getId())
                .name(bread.getName())
                .image(bread.getImage())
                .ingredients(bread.getIngredients())
                .price(bread.getPrice())
                .category(bread.getCategory())
                .avgRating(Math.round(avgRating * 10.0) / 10.0)
                .build();
    }
}

