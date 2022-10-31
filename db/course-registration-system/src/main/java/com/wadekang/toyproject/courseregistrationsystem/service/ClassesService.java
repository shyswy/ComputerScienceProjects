package com.wadekang.toyproject.courseregistrationsystem.service;

import com.wadekang.toyproject.courseregistrationsystem.controller.dto.ClassesResponseDto;
import com.wadekang.toyproject.courseregistrationsystem.controller.dto.ClassUpdateRequestDto;

import com.wadekang.toyproject.courseregistrationsystem.controller.dto.UserResponseDto;
import com.wadekang.toyproject.courseregistrationsystem.controller.dto.UserSignUpDto;
import com.wadekang.toyproject.courseregistrationsystem.domain.Classes;

import com.wadekang.toyproject.courseregistrationsystem.domain.Course;
import com.wadekang.toyproject.courseregistrationsystem.domain.TakeClass;
import com.wadekang.toyproject.courseregistrationsystem.domain.User;
import com.wadekang.toyproject.courseregistrationsystem.repository.ClassesRepository;
import com.wadekang.toyproject.courseregistrationsystem.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassesService {

    private final ClassesRepository classesRepository;
    private final CourseRepository courseRepository;

    public ClassesResponseDto findById(Long classId) {
        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Failed: No Classes Info"));

        return new ClassesResponseDto(classes);
    }

    @Transactional
    public Long update(Long classId, ClassUpdateRequestDto requestDto) {
        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Failed: No Class Info"));

        classes.update(requestDto);

        return classId;
    }


    @Transactional
    public Long save(Long courseId ,ClassUpdateRequestDto classes) {


        //if (classes.isFull()) throw new IllegalArgumentException("Failed: Full"); //수강인원 가득참.
        Course course= courseRepository.findById(courseId).get();
        Classes tmpClasses = classesRepository.save(
                Classes.builder()
                        .course(course)  //객체는 따로 생성해서 저장해야지, getCourse() 로 하면안된다!
                        .classNumber(classes.getClassNumber())
                        .professorName(classes.getProfessorName())
                        .maxStudentNum(classes.getMaxStudentNum())
                        .curStudentNum(classes.getCurStudentNum())
                        .build());




        return tmpClasses.getClassId();
    }






    @Transactional
    public void delete(Long classId) { //cancel 은 수강취소 ( 정원 반영 한다)


        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Failed: No Class Info"));

      classesRepository.delete(classes);




    }
/*
    public Classes findById(Long classId) {
        return classesRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Failed: No Class Info"));
    }
*/


    public List<Classes> findByCourse(Long courseId) { //classdto로 받아줌.
        return classesRepository.findByCourse(courseId);
    }



    /*
    public List<ClassesResponseDto> findByCourse(Long courseId) { //classdto로 받아줌.
        List<Classes> classes=classesRepository.findByCourse(courseId);
        return new ClassesResponseDto(classes);



    }

    public UserResponseDto findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Failed: No User Info"));

        return new UserResponseDto(user);
    }


     */




}
