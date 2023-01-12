package com.wadekang.toyproject.courseregistrationsystem.repository;

import com.wadekang.toyproject.courseregistrationsystem.domain.Classes;
import com.wadekang.toyproject.courseregistrationsystem.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClassesRepository extends JpaRepository<Classes, Long> {

    @Query("select c from Classes c where c.course.courseId = :courseId")
    List<Classes> findByCourse(@Param("courseId") Long courseId);

    @Query("select c from Classes c where c.averageScore is not null  order by c.averageScore desc ")
    List<Classes> findTopTen();


    @Query("select c from Classes c where c.course.major.majorId = :majorId and c.course.courseName like %:keyword% ")
    List<Classes> findByMajorAndKeyword(@Param("majorId") Long majorId, @Param("keyword") String keyword);







}
