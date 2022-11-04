package com.wadekang.toyproject.courseregistrationsystem.domain;

import com.wadekang.toyproject.courseregistrationsystem.controller.dto.ClassUpdateRequestDto;
import com.wadekang.toyproject.courseregistrationsystem.controller.dto.UserUpdateRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Classes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_id", nullable = false)
    private Long classId;

    @ManyToOne(targetEntity = Course.class, fetch = FetchType.LAZY) ///여기부터!!
    @JoinColumn(name="course_id")
    private Course course;

    @Column(nullable = false)
    private int classNumber;

    @Column(nullable = false)
    private String professorName;

    @Column(nullable = false)
    private int maxStudentNum;

    @Column(nullable = false)
    private int curStudentNum;


    @OneToMany(mappedBy = "classes")
    private List<TakeClass> takeClasses;


    @OneToMany(mappedBy = "classes")
    private List<ClassTime> classTimes;

    @OneToMany(mappedBy = "classes")
    private List<Credit> credits;

    @Column
    private Long averageScore;



    @Builder
    public Classes(Course course, int classNumber, String professorName, int maxStudentNum, int curStudentNum) {
        this.course = course;
        this.classNumber = classNumber;
        this.professorName = professorName;
        this.maxStudentNum = maxStudentNum;
        this.curStudentNum = curStudentNum;

        this.classTimes=new ArrayList<>();
        this.credits=new ArrayList<>();
        this.takeClasses=new ArrayList<>();
        this.averageScore=0L;
    }



    public void update(ClassUpdateRequestDto requestDto) {
        this.maxStudentNum = requestDto.getMaxStudentNum();
        this.averageScore=requestDto.getAverageScore();

    }

    //== 수강 신청 ==//
    public void registration() {
        this.curStudentNum++;
    }



    //== 수강 취소 ==//
    public void cancel() {
        this.curStudentNum--;
    }

    //== 수강 인원 확인 ==//
    public boolean isFull() {
        return curStudentNum >= maxStudentNum;
    }
}
