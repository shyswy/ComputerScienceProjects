package com.wadekang.toyproject.courseregistrationsystem.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class UserUpdateRequestDto {
    //서비스 계층에서 DTO 이용해서 필요한 내용을 전달 받고, 반환하도록 처리하도록
    // service 패키지의 Service 인터페이스와 ServiceImpl 클래스 추가
    private String loginId;
    private String username;
    private String email;
    private String phoneNumber;
    private String majorName;

    private Long averageScore;



    public UserUpdateRequestDto(String loginId, String username, String email, String phoneNumber, String majorName) {
        this.loginId = loginId;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.majorName = majorName;


    }

    public UserUpdateRequestDto(UserResponseDto userResponseDto) {
        this.loginId = userResponseDto.getLoginId();
        this.username = userResponseDto.getUsername();
        this.email = userResponseDto.getEmail();
        this.phoneNumber = userResponseDto.getPhoneNumber();
        this.majorName = userResponseDto.getMajor().getMajorName();
        this.averageScore=userResponseDto.getAverageScore();
    }
}
