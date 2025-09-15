package org.example.startapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.startapi.domain.entity.Reply;
import org.example.startapi.domain.entity.User;
import org.example.startapi.domain.entity.UserQna;
import org.example.startapi.dto.*;
import org.example.startapi.service.BreadService;
import org.example.startapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "사용자 & 공개 API", description = "로그인, 회원가입, QnA, 상품 조회 등 사용자 및 비로그인 상태에서 접근 가능한 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final BreadService breadService;

    // --- 로그인/회원가입 API ---

    @Operation(summary = "ID/PW 로그인", description = "사용자 아이디(이메일)와 비밀번호를 사용하여 로그인하고, 성공 시 사용자 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (아이디 또는 비밀번호 불일치)")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO loginRequest) {
        User loginUser = userService.login(loginRequest.getUserId(), loginRequest.getPassword());
        if (loginUser != null) {
            UserDTO responseDto = convertToUserDto(loginUser);
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }
    }

    @Operation(summary = "핀번호 로그인", description = "등록된 핀번호를 사용하여 간편 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (핀번호 불일치)")
    })
    @PostMapping("/login_pinNo")
    public ResponseEntity<?> loginPinNo(@RequestBody UserDTO pinNoRequest) {
        User pinNoUser = userService.pinNoLogin(pinNoRequest.getPinNo());
        if (pinNoUser != null) {
            UserDTO responseDto = convertToUserDto(pinNoUser);
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "회원가입", description = "새로운 사용자 정보를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "409", description = "회원가입 실패 (아이디 또는 전화번호 중복)")
    })
    @PostMapping("/joinUser")
    public ResponseEntity<?> joinUser(@RequestBody UserDTO joinUserRequest) {
        boolean isRegistered = userService.registerUser(joinUserRequest);
        if (isRegistered) {
            return ResponseEntity.ok("회원가입 성공");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("중복된 아이디 또는 전화번호입니다.");
        }
    }

    // --- QnA 관련 API ---

    @Operation(summary = "1:1 문의 등록", description = "사용자가 새로운 1:1 문의를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "문의 등록 성공")
    @PostMapping("/qna")
    public ResponseEntity<?> qna(@RequestBody UserQnaDTO qnaRequest) {
        UserQna savedQna = userService.qna(qnaRequest);
        if (savedQna != null) {
            UserQnaDTO responseDto = convertToQnaDto(savedQna);
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("등록 불가");
        }
    }

    @Operation(summary = "내 문의 내역 조회", description = "전화번호를 기준으로 사용자가 등록한 모든 QnA 목록과 답변을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/qna/list")
    public ResponseEntity<List<UserQnaDTO>> getQnaListByPhone(
            @Parameter(description = "조회할 사용자의 전화번호", required = true, example = "010-1234-5678")
            @RequestParam String phone) {
        List<UserQna> qnaList = userService.getQnaListByPhone(phone);
        List<UserQnaDTO> responseDtoList = qnaList.stream()
                .map(this::convertToQnaDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtoList);
    }

    @Operation(summary = "내 문의 내역 삭제", description = "문의 ID와 전화번호를 기준으로 특정 QnA 내역을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "삭제할 항목을 찾을 수 없음")
    })
    @DeleteMapping("/qna/delete")
    public ResponseEntity<?> deleteQnaByTime(
            @Parameter(description = "삭제할 QnA의 ID", required = true, example = "1") @RequestParam int id,
            @Parameter(description = "본인 확인을 위한 전화번호", required = true, example = "010-1234-5678") @RequestParam String phone) {
        boolean isDeleted = userService.deleteQna(id, phone);
        if (isDeleted) {
            return ResponseEntity.ok("삭제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("삭제할 항목을 찾을 수 없습니다.");
        }
    }

    // --- 사용자 정보 변경 API ---

    @Operation(summary = "비밀번호 변경", description = "전화번호로 본인 확인 후 비밀번호를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "409", description = "변경 실패 (사용자를 찾을 수 없거나 기존 비밀번호와 동일)")
    })
    @PostMapping("/changePw")
    public ResponseEntity<?> changePw(@RequestBody UserChangePasswordDTO changePwRequest) {
        boolean result = userService.changePassword(changePwRequest.getNewPassword(), changePwRequest.getPhoneNumber());
        if (result) {
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("변경 실패: 사용자를 찾을 수 없거나 기존 비밀번호와 동일합니다.");
        }
    }

    @Operation(summary = "핀번호 등록", description = "전화번호로 사용자를 찾아 핀번호를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "핀번호 등록 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/register_pinNo")
    public ResponseEntity<?> registerPinNo(@RequestBody UserDTO pinNoRequest) {
        boolean isRegistered = userService.registerPinNo(pinNoRequest.getPinNo(), pinNoRequest.getPhoneNumber());
        if (isRegistered) {
            return ResponseEntity.ok("Pin번호 등록이 완료되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
    }

    @Operation(summary = "주소 변경", description = "전화번호로 사용자를 찾아 주소를 변경합니다.")
    @ApiResponse(responseCode = "200", description = "주소 변경 성공")
    @PostMapping("/changeAddress")
    public ResponseEntity<?> changeAddress(@RequestBody UserDTO userRequest) {
        User updatedUser = userService.changeAddress(userRequest.getPhoneNumber(), userRequest.getUserAddress());
        if (updatedUser != null) {
            UserDTO responseDto = convertToUserDto(updatedUser);
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없어 변경에 실패했습니다.");
        }
    }

    // --- 사용자 정보 조회 API ---

    @Operation(summary = "주소 조회", description = "전화번호로 사용자의 주소 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/getAddress")
    public ResponseEntity<?> getAddress(
            @Parameter(description = "조회할 사용자의 전화번호", required = true, example = "010-1234-5678")
            @RequestParam String phoneNumber) {
        User user = userService.getUserAddress(phoneNumber);
        if (user != null) {
            UserDTO responseDto = convertToUserDto(user);
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("조회 실패");
        }
    }

    @Operation(summary = "포인트 조회", description = "전화번호로 사용자의 포인트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/userPoint")
    public ResponseEntity<?> getUserPoint(
            @Parameter(description = "조회할 사용자의 전화번호", required = true, example = "010-1234-5678")
            @RequestParam String phoneNumber) {
        User pointResult = userService.getUserPoint(phoneNumber);
        if (pointResult != null) {
            UserDTO responseDto = convertToUserDto(pointResult);
            return ResponseEntity.ok(responseDto.getUserPoint());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("포인트 조회실패");
        }
    }

    // --- 상품(빵) 정보 조회 API ---

    @Operation(summary = "오늘의 빵 목록 조회", description = "카테고리별로 그룹화된 오늘의 빵 목록과 평균 평점을 함께 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/todayBread")
    public ResponseEntity<Map<String, List<TodayBreadDTO>>> getTodayBreads() {
        Map<String, List<TodayBreadDTO>> categorizedBreads = breadService.getTodayBreadsCategorized();
        return ResponseEntity.ok(categorizedBreads);
    }


    // --- Helper Methods ---

    private UserDTO convertToUserDto(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .userPoint(user.getUserPoint())
                .phoneNumber(user.getPhoneNumber())
                .pinNo(user.getPinNo())
                .userAddress(user.getUserAddress())
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

