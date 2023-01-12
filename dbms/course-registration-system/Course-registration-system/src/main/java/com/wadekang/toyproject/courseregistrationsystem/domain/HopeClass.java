package com.wadekang.toyproject.courseregistrationsystem.domain;




import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class HopeClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hope_id", nullable = false)
    private Long hopeId;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(targetEntity = Classes.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private Classes classes;

    @Builder
    public HopeClass(Long hopeId,User user,Classes classes){
        this.hopeId=hopeId;
        this.user=user;
        this.classes=classes;
    }
}
