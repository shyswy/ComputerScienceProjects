package com.wadekang.toyproject.courseregistrationsystem.domain;

import com.wadekang.toyproject.courseregistrationsystem.controller.dto.ClassUpdateRequestDto;
import com.wadekang.toyproject.courseregistrationsystem.controller.dto.UserUpdateRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

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


    @OneToMany(mappedBy = "classes",orphanRemoval = true) //orphanRemoval = true >> classes 삭제시 관련된 takeclasses 들도 삭제된다.
    private List<TakeClass> takeClasses;

    @OneToMany(mappedBy = "classes",orphanRemoval = true) //orphanRemoval = true >> classes 삭제시 관련된 takeclasses 들도 삭제된다.
    private List<HopeClass> hopeClasses;


    //@OneToMany(mappedBy = "classes")
    //private List<ClassTime> classTimes;

    @OneToMany(mappedBy = "classes")
    private List<Credit> credits;

    @Column
    private Long averageScore;

    @ManyToOne(targetEntity = Room.class,fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    //@Column(nullable = false)
    //private Long classTime;

    @Column(nullable = false)
    private Long startTime; //30분단위. 1~48 로 24시간 표현

    @Column(nullable = false)
    private Long endTime;

    @Column(nullable = false)
    private Long Day; // 1~ 7  월~ 일

    @Column(nullable = false)
    private Long gradeAmount;




    @Builder
    public Classes(Course course, int classNumber, String professorName, int maxStudentNum, int curStudentNum,Room room
    ,Long Day, Long startTime, Long endTime,Long gradeAmount) {
        this.course = course;
        this.classNumber = classNumber;
        this.professorName = professorName;
        this.maxStudentNum = maxStudentNum;
        this.curStudentNum = curStudentNum;

        //this.classTimes=new ArrayList<>();
        this.credits=new ArrayList<>();
        this.takeClasses=new ArrayList<>();
        this.averageScore=0L;
        this.room=room;
        this.Day=Day;
        this.startTime=startTime;
        this.endTime=endTime;
        this.gradeAmount=gradeAmount;

    }



    public void update(ClassUpdateRequestDto requestDto) {
        this.maxStudentNum = requestDto.getMaxStudentNum();
        this.averageScore=requestDto.getAverageScore();

    }


      /*
    //== 수강 신청 ==//
    public void registration() {
        this.curStudentNum++;
    }
    //== 수강 취소 ==//
    public void cancel() {
        this.curStudentNum--;
    }
    */

    public void SetCurStudent(int curStudentNum){  //수강 신청, 취소 변수 너무 많다. ( 작년, 수강 정정, 등등 다관리하는데 +,- 너무 복잡해짐)
        //아예 그떄그떄 grade ==99L ( default 인 사람) 수를 세어준 뒤, set 해준다.
        this.curStudentNum=curStudentNum;
    }

    //== 수강 인원 확인 ==//
    public boolean isFull() {
        return curStudentNum >= maxStudentNum;
    }


}
