package com.wadekang.toyproject.courseregistrationsystem.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="room_id",nullable = false)
    private Long roomId;

    //@OneToMany(mappedBy = "classes")
    @Column(name="room_no",nullable = false)
    private Long roomNo;

    @Column(name="max_person",nullable = false)
    private Long maxPerson;

    @Column(name="building_name",nullable = false)
    private String buildingName;

    @Builder
    public Room(Long roomId,Long roomNo,Long maxPerson){
        this.roomId=roomId;
        this.roomNo=roomNo;
        this.maxPerson=50L; //기본 정원 50명
        this.buildingName="default";
    }

}
