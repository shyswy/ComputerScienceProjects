package com.wadekang.toyproject.courseregistrationsystem.controller.dto;

import com.wadekang.toyproject.courseregistrationsystem.domain.*;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ClassesResponseDto {
    private Long classId;


    private Course course;


    private int classNumber;


    private String professorName;


    private int maxStudentNum;


    private Long courseid;

    private int curStudentNum;

    public ClassesResponseDto(Classes entity) {
        this.maxStudentNum = entity.getMaxStudentNum();
        this.classId= entity.getClassId();
        this.course=entity.getCourse();
        this.classNumber=entity.getClassNumber();
        this.professorName=entity.getProfessorName();

    }
}
