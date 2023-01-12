package com.wadekang.toyproject.courseregistrationsystem.controller.dto;

import com.wadekang.toyproject.courseregistrationsystem.domain.*;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class UserResponseDto {

    private Long userId;
    private String loginId;
    private String username;
    private String email;
    private String phoneNumber;
    private Major major;
    private List<TakeClass> takeClasses;

    private List<HopeClass> hopeClasses;

    private Long averageScore;

    private Long thisYearGrade;

    private Long doneGrade;






    public UserResponseDto(User entity) {
        this.userId = entity.getUserId();
        this.loginId = entity.getLoginId();
        this.username = entity.getUsername();
        this.email = entity.getEmail();
        this.phoneNumber = entity.getPhoneNumber();
        this.major = entity.getMajor();
        this.takeClasses = entity.getTakeClasses();
        this.hopeClasses=entity.getHopeClasses();
        this.averageScore= entity.getAverageScore();
        this.thisYearGrade=entity.getThisYearGrade();
        this.doneGrade=entity.getDoneGrade();



    }
}
