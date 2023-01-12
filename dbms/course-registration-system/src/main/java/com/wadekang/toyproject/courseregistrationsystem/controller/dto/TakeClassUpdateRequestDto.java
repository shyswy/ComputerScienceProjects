package com.wadekang.toyproject.courseregistrationsystem.controller.dto;


import com.wadekang.toyproject.courseregistrationsystem.domain.Course;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor

public class TakeClassUpdateRequestDto {



    private Long takeId;


    private Long grade;

    private Long userId;

    private Long classId;



    public TakeClassUpdateRequestDto(Long takeId,Long grade,Long userId) {
        this.takeId=takeId;
        this.grade = grade;
        this.userId=userId;


    }

    /*
    public ClassUpdateRequestDto(ClassesResponseDto classesResponseDto) {
       this.maxStudentNum = classesResponseDto.getMaxStudentNum();
    }
*/






}
