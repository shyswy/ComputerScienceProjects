package com.wadekang.toyproject.courseregistrationsystem.repository;

import com.wadekang.toyproject.courseregistrationsystem.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room,Long> {
}
