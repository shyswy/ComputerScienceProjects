package com.wadekang.toyproject.courseregistrationsystem.service;

import com.wadekang.toyproject.courseregistrationsystem.controller.dto.ClassUpdateRequestDto;
import com.wadekang.toyproject.courseregistrationsystem.domain.Classes;
import com.wadekang.toyproject.courseregistrationsystem.domain.Course;
import com.wadekang.toyproject.courseregistrationsystem.domain.Major;
import com.wadekang.toyproject.courseregistrationsystem.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;

    public List<Course> findByMajor(Long majorId) {
        return courseRepository.findByMajor(majorId);
    }

    public Course findById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Failed: No Course Info"));
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }


    @Transactional
    public Long save(Course cc) {


        //if (classes.isFull()) throw new IllegalArgumentException("Failed: Full"); //수강인원 가득참.

        Course tmpcourse = courseRepository.save(
                Course.builder()
                        .major(cc.getMajor())
                        .courseName(cc.getCourseName())
                        .classes(cc.getClasses())

                        .build());




        return tmpcourse.getCourseId();
    }




}
