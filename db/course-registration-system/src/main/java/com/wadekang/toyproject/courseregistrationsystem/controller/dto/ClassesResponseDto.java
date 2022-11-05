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

    private Long averageScore;


    private Long courseid;

    private int curStudentNum;

    private Long roomId;

    private Long startTime;

    private Long endTime;

    private Long day;


    public ClassesResponseDto(Classes entity) {
        this.maxStudentNum = entity.getMaxStudentNum();
        this.classId= entity.getClassId();
        this.course=entity.getCourse();
        this.classNumber=entity.getClassNumber();
        this.professorName=entity.getProfessorName();
        this.averageScore=entity.getAverageScore();
        this.roomId=entity.getRoom().getRoomId();
        this.startTime=entity.getStartTime();
        this.endTime=entity.getEndTime();
        this.day=entity.getEndTime();

    }
}
