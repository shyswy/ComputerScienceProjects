package com.wadekang.toyproject.courseregistrationsystem.domain;

import com.wadekang.toyproject.courseregistrationsystem.controller.dto.ClassUpdateRequestDto;
import com.wadekang.toyproject.courseregistrationsystem.controller.dto.TakeClassUpdateRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Null;

@Entity
@NoArgsConstructor
@Getter
public class TakeClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "take_id", nullable = false)
    private Long takeId;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(targetEntity = Classes.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private Classes classes;

    @Column(nullable = false)
    private Long grade;

    @Builder
    public TakeClass(User user, Classes classes) {
        this.user = user;
        this.classes = classes;
        this.grade=99L;//디폴트 학점 디폴트 null 하면 평균에서 제거 가능, 아직 prob 발생.
    }

    public void update(TakeClassUpdateRequestDto requestDto) {
        this.grade=requestDto.getGrade();

    }
}
