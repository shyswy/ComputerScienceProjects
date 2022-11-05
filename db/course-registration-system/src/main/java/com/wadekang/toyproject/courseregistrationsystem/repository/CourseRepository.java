package com.wadekang.toyproject.courseregistrationsystem.repository;

import com.wadekang.toyproject.courseregistrationsystem.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("select c from Course c where c.major.majorId = :majorId")
    List<Course> findByMajor(@Param("majorId") Long majorId);

    @Query("select c from Course c where c.major.majorId = :majorId and c.courseName like %:keyword% ")
    List<Course> findByMajorAndKeyword(@Param("majorId") Long majorId, @Param("keyword") String keyword);
}
