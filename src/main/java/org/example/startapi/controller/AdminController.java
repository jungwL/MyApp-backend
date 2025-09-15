package org.example.startapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.startapi.domain.entity.*;
import org.example.startapi.dto.*;
import org.example.startapi.service.AdminService;
import org.example.startapi.service.BreadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "관리자 API", description = "관리자 전용 기능 (사용자, QnA, 상품 관리 등)을 제공하는 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final BreadService breadService;

    //관리자페이지 로그인 호출
    @PostMapping("/adminLogin")
    public ResponseEntity<?> adminLogin(@RequestBody AdminDTO adminDTO) {
        Admin rsAdmin = adminService.adminLogin(adminDTO.getAdminId() , adminDTO.getAdminPassword());
        if (rsAdmin != null) {
            return ResponseEntity.ok(convertToAdminDto(rsAdmin));
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인실패");
        }
    }

    // --- 사용자 관리 API ---
    @Operation(summary = "모든 사용자 목록 조회", description = "데이터베이스에 등록된 모든 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/users/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> userList = adminService.getAllUsers();
        List<UserDTO> responseDtoList = userList.stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtoList);
    }

    @Operation(summary = "특정 사용자 정보 수정", description = "특정 ID의 사용자 정보를 전달된 데이터로 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 사용자를 찾을 수 없음")
    })
    @PutMapping("/users/{userId}")
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "수정할 사용자의 고유 ID (이메일 형식)", required = true, example = "user1@example.com")
            @PathVariable String userId,
            @RequestBody UserDTO userDTO) {
        try {
            User updatedUser = adminService.updateUser(userId, userDTO);
            return ResponseEntity.ok(convertToUserDto(updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- 빵 관리 API ---
    @Operation(summary = "오늘의 빵 전체 목록 조회", description = "카테고리별로 그룹화된 오늘의 빵 목록 전체를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "빵 목록 조회 성공")
    @GetMapping("/todayBreadList/all")
    public ResponseEntity<Map<String, List<TodayBreadDTO>>> getTodayBreads() {
        Map<String, List<TodayBreadDTO>> categorizedBreads = breadService.getTodayBreadsCategorized();
        return ResponseEntity.ok(categorizedBreads);
    }

    // --- QnA 관리 API ---
    @Operation(summary = "모든 QnA 목록 조회 (답변 포함)", description = "모든 QnA 목록을 조회하며, 각 QnA에 달린 답변 목록도 함께 반환합니다.")
    @ApiResponse(responseCode = "200", description = "QnA 목록 조회 성공")
    @GetMapping("/qna/all")
    public ResponseEntity<List<UserQnaDTO>> getAllQnaList() {
        List<UserQna> qnaList = adminService.getAllQna();
        List<UserQnaDTO> responseDtoList = qnaList.stream()
                .map(this::convertToQnaDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtoList);
    }

    @Operation(summary = "QnA에 답변 등록", description = "특정 ID의 QnA에 새로운 답변을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "답변 등록 성공"),
            @ApiResponse(responseCode = "400", description = "답변 내용이 비어있음"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 QnA를 찾을 수 없음")
    })
    @PostMapping("/qna/{qnaId}/reply")
    public ResponseEntity<String> addReply(
            @Parameter(description = "답변을 등록할 QnA의 ID", required = true, example = "1")
            @PathVariable Long qnaId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "등록할 답변 내용",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Map.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"reply\": \"고객님의 소중한 의견 감사합니다.\"}")))
            @RequestBody Map<String, String> payload) {

        String replyContent = payload.get("reply");
        if (replyContent == null || replyContent.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("답변 내용이 비어있습니다.");
        }

        boolean isSuccess = adminService.addReplyToQna(qnaId, replyContent);

        if (isSuccess) {
            return ResponseEntity.ok("답글이 성공적으로 등록되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 문의를 찾을 수 없습니다.");
        }
    }
    // -- 오늘의 빵 정보 삭제 api
    @DeleteMapping("/todayBreadDel/{breadId}")
    public ResponseEntity<String> deleteBread(@PathVariable Long breadId) {
        Boolean result = breadService.delTodayBreadList(breadId);
        if (result) {
            return ResponseEntity.ok("빵 정보가 삭제가 완료되었습니다");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 상품을 찾을 수 없습니다.");
        }
    }

    // -- 오늘의 빵 정보 상품 추가 api
    @PostMapping("/todayBreadSub")
    public ResponseEntity<?> addBreadSub(@RequestBody TodayBreadDTO requsetTodayBreadDTO ) {
        System.out.println("---오늘의 빵 상품 등록 호출 ---");
        System.out.println("카테고리 : " + requsetTodayBreadDTO.getCategory());
        System.out.println("상품명 : " + requsetTodayBreadDTO.getName());
        System.out.println("가격 : " + requsetTodayBreadDTO.getPrice());
        System.out.println("이미지 " + requsetTodayBreadDTO.getImage());
        TodayBreadDTO saveTodayBread  = breadService.subTodayBread(requsetTodayBreadDTO);
        if(saveTodayBread != null) {
            return ResponseEntity.ok(saveTodayBread);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("등록불가");
        }
    }

    // -- 주문내역 정보 가져오기
    @GetMapping("/orderList/all")
    // 2. 반환 타입을 List<OrderDTO>로 명확하게 지정
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        // 3. 서비스는 엔티티 리스트(List<Order>)를 반환하도록 수정되어야 합니다.
        List<Order> allOrders = adminService.getAllOrderList();
        // 4. 엔티티 리스트를 DTO 리스트로 변환하는 로직
        List<OrderDTO> responseDtoList = allOrders.stream()
                .map(this::convertToOrderDto) // 각 Order를 OrderDTO로 변환
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtoList);
    }

    // --- Helper Method ---

    /**
     * Order 엔티티를 OrderDTO로 변환하는 헬퍼 메소드.
     * 연관된 User와 TodayBread의 정보도 함께 DTO에 담아줍니다.
     */
    private OrderDTO convertToOrderDto(Order order) {
        // User 엔티티에서 필요한 정보만 추출하여 UserInfo DTO 생성
        OrderDTO.UserInfo userInfo = OrderDTO.UserInfo.builder()
                .userId(order.getUser().getUserId())
                .userName(order.getUser().getUserName())
                .build();

        // TodayBread 엔티티에서 필요한 정보만 추출하여 BreadInfo DTO 생성
        OrderDTO.BreadInfo breadInfo = OrderDTO.BreadInfo.builder()
                .id(order.getTodayBread().getId())
                .name(order.getTodayBread().getName())
                .category(order.getTodayBread().getCategory())
                .build();

        // 최종 OrderDTO를 생성하여 반환
        return OrderDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderType(order.getOrderType())
                .orderName(order.getOrderName())
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .orderDate(order.getOrderDate().toString())
                .user(userInfo)
                .todayBread(breadInfo)
                .build();
    }

    private AdminDTO convertToAdminDto(Admin admin) {
        return AdminDTO.builder()
                .adminId(admin.getAdminId())
                .adminPassword(admin.getAdminPassword())
                .adminName(admin.getAdminName())
                .adminPhoneNumber(admin.getAdminPhoneNumber())
                .build();
    }

    private UserDTO convertToUserDto(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .phoneNumber(user.getPhoneNumber())
                .userAddress(user.getUserAddress())
                .userPoint(user.getUserPoint())
                .pinNo(user.getPinNo())
                .build();
    }

    private UserQnaDTO convertToQnaDto(UserQna qna) {
        List<ReplyDTO> replyDTOs = qna.getReplies().stream()
                .map(this::convertToReplyDto)
                .collect(Collectors.toList());

        return UserQnaDTO.builder()
                .id(qna.getId())
                .consultType(qna.getConsultType())
                .contentType(qna.getContentType())
                .title(qna.getTitle())
                .content(qna.getContent())
                .name(qna.getName())
                .phone(qna.getPhone())
                .email(qna.getEmail())
                .addTime(qna.getAddTime() != null ? qna.getAddTime().toString() : null)
                .replies(replyDTOs)
                .build();
    }

    private ReplyDTO convertToReplyDto(Reply reply) {
        return ReplyDTO.builder()
                .id(reply.getId())
                .content(reply.getContent())
                .createdAt(reply.getCreatedAt() != null ? reply.getCreatedAt().toString() : null)
                .qnaId(reply.getUserQna().getId())
                .build();
    }
}

