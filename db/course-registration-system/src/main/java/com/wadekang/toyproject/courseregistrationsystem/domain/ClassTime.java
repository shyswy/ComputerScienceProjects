package com.wadekang.toyproject.courseregistrationsystem.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class ClassTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //P.K 는 자동생성되게
    @Column(name = "time_id", nullable = false)
    private Long timeId;


    //@ManyToOne(targetEntity = Classes.class, fetch = FetchType.LAZY)
    //@JoinColumn(name="class_id")
    //private Classes classes;

    @Column(nullable = false)
    private Long day;
    @Column(nullable = false)
    private Long startTime;
    @Column(nullable = false)
    private Long endTime;


    @Builder
    public ClassTime(Classes classes,Long day,Long startTime,Long endTime) {
        //this.classes=classes;
        this.day=day;
        this.startTime=startTime;
        this.endTime=endTime;
    }
}

