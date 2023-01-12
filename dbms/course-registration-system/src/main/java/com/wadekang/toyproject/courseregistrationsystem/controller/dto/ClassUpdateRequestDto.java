package com.wadekang.toyproject.courseregistrationsystem.controller.dto;


import com.wadekang.toyproject.courseregistrationsystem.domain.Course;
import com.wadekang.toyproject.courseregistrationsystem.repository.ClassesRepository;
import com.wadekang.toyproject.courseregistrationsystem.service.ClassesService;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@NoArgsConstructor

public class ClassUpdateRequestDto {




    private int maxStudentNum;


    private int curStudentNum;

    private Long classId;


    private Long courseId;
    private Course course;  //// 과목 추가시에 course>> 입력받기 !!! html에서 course 객체 정보 만들기..?


    private int classNumber;


    private String professorName;

    private Long averageScore;

    private Long roomId;

    private Long startTime;

    private Long endTime;

    private Long day;

    private Long gradeAmount;




    public ClassUpdateRequestDto(int max_student_num) {
        this.maxStudentNum = max_student_num;
    }

    /*
    public ClassUpdateRequestDto(ClassesResponseDto classesResponseDto) {
       this.maxStudentNum = classesResponseDto.getMaxStudentNum();
    }
*/

    @Builder
    public ClassUpdateRequestDto(ClassesResponseDto classesResponseDto) {
        this.maxStudentNum=classesResponseDto.getMaxStudentNum();
        this.curStudentNum=classesResponseDto.getCurStudentNum();
        this.course=classesResponseDto.getCourse();
        this.classId=classesResponseDto.getClassId();
        this.classNumber=classesResponseDto.getClassNumber();
        this.professorName=classesResponseDto.getProfessorName();
        this.averageScore=classesResponseDto.getAverageScore();
        this.roomId= classesResponseDto.getRoomId();
        this.day=classesResponseDto.getDay();
        this.startTime=classesResponseDto.getStartTime();
        this.endTime=classesResponseDto.getEndTime();
        this.gradeAmount=classesResponseDto.getGradeAmount();

    }

    @Builder
    public ClassUpdateRequestDto(int maxStudentNum,int curStudentNum,Long classId, Course course, int classNumber,String professorName, Long averageScore, Long roomId
    ,Long startTime,Long endTime, Long day , Long gradeAmount) {
      this.maxStudentNum=maxStudentNum;
      this.curStudentNum=curStudentNum;
      this.classId=classId;
      this.course= course ;
      this.classNumber=classNumber;
      this.professorName=professorName;
      this.averageScore=averageScore;
      this.roomId=roomId;
      this.startTime=startTime;
      this.endTime=endTime;
      this.day=day;
      this.gradeAmount=gradeAmount;
    }


}
