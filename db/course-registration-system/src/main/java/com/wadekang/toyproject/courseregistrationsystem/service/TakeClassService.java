package com.wadekang.toyproject.courseregistrationsystem.service;

import com.wadekang.toyproject.courseregistrationsystem.domain.Classes;
import com.wadekang.toyproject.courseregistrationsystem.domain.TakeClass;
import com.wadekang.toyproject.courseregistrationsystem.domain.User;
import com.wadekang.toyproject.courseregistrationsystem.repository.ClassesRepository;
import com.wadekang.toyproject.courseregistrationsystem.repository.TakeClassRepository;
import com.wadekang.toyproject.courseregistrationsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TakeClassService {

    private final TakeClassRepository takeClassRepository;
    private final UserRepository userRepository;
    private final ClassesRepository classesRepository;

    @Transactional
    public Long save(Long userId, Long classId) {
        User user = userRepository.findById(userId).get();
        Classes classes = classesRepository.findById(classId).get();

        if (classes.isFull()) throw new IllegalArgumentException("Failed: Full"); //수강인원 가득참.

        Optional<TakeClass> any = user.getTakeClasses().stream()
                .filter(takeClass ->
                        takeClass.getClasses().getCourse().getCourseId().equals(classes.getCourse().getCourseId())).findAny();

        if (any.isPresent()) throw new IllegalArgumentException("Failed: Already Registered!"); //이미 해당과목 신청

        TakeClass takeClass = takeClassRepository.save(
                TakeClass.builder()
                    .user(user)
                    .classes(classes)
                    .build());

        user.registration(takeClass);
        classes.registration();

        return takeClass.getTakeId();
    }

    @Transactional
    public void delete(Long takeId) {
        TakeClass takeClass = takeClassRepository.findById(takeId).get();

        User user = userRepository.findById(takeClass.getUser().getUserId()).get();
        user.cancel(takeClass);

        Classes classes = classesRepository.findById(takeClass.getClasses().getClassId()).get();
        classes.cancel();

        takeClassRepository.delete(takeClass);

    }
}
