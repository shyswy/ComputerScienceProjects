package com.wadekang.toyproject.courseregistrationsystem.service;

import com.wadekang.toyproject.courseregistrationsystem.domain.*;
import com.wadekang.toyproject.courseregistrationsystem.repository.ClassesRepository;
import com.wadekang.toyproject.courseregistrationsystem.repository.HopeClassRepository;
import com.wadekang.toyproject.courseregistrationsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HopeClassService {

    private final HopeClassRepository hopeClassRepository;
    private final UserRepository userRepository;

    private final ClassesRepository classesRepository;

    public List<HopeClass> findAll() {
        return hopeClassRepository.findAll();
    }

    @Transactional
    public Long save(Long userId, Long classId) {
        User user = userRepository.findById(userId).get();
        Classes classes = classesRepository.findById(classId).get();


        if (classes.isFull()) throw new IllegalArgumentException("Failed: Full"); //수강인원 가득참.

        Optional<HopeClass> any = user.getHopeClasses().stream()
                .filter(hopeClass ->
                        hopeClass.getClasses().getCourse().getCourseId().equals(classes.getCourse().getCourseId())).findAny();

        if (any.isPresent()) throw new IllegalArgumentException("Failed: Already in Hope List!"); //이미 해당과목 신청



        HopeClass hopeClass1=hopeClassRepository.save(
                HopeClass.builder()
                        .user(user)
                        .classes(classes)
                        .build()
        );

        user.hopeRegistration(hopeClass1);

        return hopeClass1.getHopeId();
    }


    @Transactional
    public void delete(Long hopeId) {
        HopeClass hopeClass = hopeClassRepository.findById(hopeId).get();

        User user = userRepository.findById(hopeClass.getUser().getUserId()).get();
        user.cancelHope(hopeClass);

        Classes classes = classesRepository.findById(hopeClass.getClasses().getClassId()).get();

        hopeClassRepository.delete(hopeClass);

    }

    public HopeClass findById(Long hopeId){
        return hopeClassRepository.findById(hopeId)
                .orElseThrow(() -> new IllegalArgumentException("Failed: No Hope Info"));

    }
}
