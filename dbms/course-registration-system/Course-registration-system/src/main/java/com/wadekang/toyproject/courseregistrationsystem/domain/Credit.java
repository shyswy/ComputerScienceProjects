package com.wadekang.toyproject.courseregistrationsystem.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@NoArgsConstructor
@Getter
public class Credit {

    //credit 버리기!!!!

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credit_id", nullable = false)
    private Long creditId;


    @ManyToOne(targetEntity = Classes.class, fetch = FetchType.LAZY)
    @JoinColumn(name="class_id")
    private Classes classes;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @Column(nullable = false)
    private Long grade;



    @Builder
    public Credit(Classes classes,User user,Long grade) {

        this.classes=classes;
        this.user=user;
        this.grade=grade;

    }
}
