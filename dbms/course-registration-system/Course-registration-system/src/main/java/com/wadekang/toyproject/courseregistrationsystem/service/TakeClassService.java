package com.wadekang.toyproject.courseregistrationsystem.service;

import com.wadekang.toyproject.courseregistrationsystem.controller.dto.ClassUpdateRequestDto;
import com.wadekang.toyproject.courseregistrationsystem.controller.dto.TakeClassUpdateRequestDto;
import com.wadekang.toyproject.courseregistrationsystem.controller.dto.UserResponseDto;
import com.wadekang.toyproject.courseregistrationsystem.controller.dto.UserUpdateRequestDto;
import com.wadekang.toyproject.courseregistrationsystem.domain.Classes;
import com.wadekang.toyproject.courseregistrationsystem.domain.TakeClass;
import com.wadekang.toyproject.courseregistrationsystem.domain.User;
import com.wadekang.toyproject.courseregistrationsystem.repository.ClassesRepository;
import com.wadekang.toyproject.courseregistrationsystem.repository.TakeClassRepository;
import com.wadekang.toyproject.courseregistrationsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TakeClassService {

    private final TakeClassRepository takeClassRepository;
    private final UserRepository userRepository;
    private final ClassesRepository classesRepository;

    private final UserService userService;

    @Transactional
    public Long save(Long userId, Long classId) {
        User user = userRepository.findById(userId).get();
        Classes classes = classesRepository.findById(classId).get();
        Long ThisYearGradeAmount= user.getThisYearGrade();
        if(ThisYearGradeAmount==null)
            ThisYearGradeAmount=0L;

        if( ThisYearGradeAmount+ classes.getGradeAmount() > 18L )
            throw new IllegalArgumentException("Failed: cannot attend Over 18!");

        if( ! takeClassRepository.findNoAgain(userId,classId).isEmpty() )
            throw new IllegalArgumentException("Failed: above B0 cannot re attend!");

        if (classes.isFull()) throw new IllegalArgumentException("Failed: Full"); //수강인원 가득참.

        Optional<TakeClass> any = user.getTakeClasses().stream()// 같은 과목인것 이미 수강했다 filter
                .filter(takeClass ->
                        takeClass.getClasses().getCourse().getCourseId().equals(classes.getCourse().getCourseId())).findAny();


        if (any.isPresent() ) throw new IllegalArgumentException("Failed: Already Registered!"); //이미 해당과목 신청

        List<TakeClass> classesList=takeClassRepository.findRepeat(userId,classes.getDay(),classes.getStartTime(),classes.getEndTime());
        //List<Classes> classesList=classesRepository.findRepeat(classes);
        if( !( classesList.isEmpty() )  )
            throw new IllegalArgumentException("Failed: classTime is repeated");
        // 시간대 중복 체크.  day 중복하고,   시작 or 종료 시간중 하나라도 기존 시작, 종료 시간 사이에 존재시 불가능!
        // old 시작이 new 종료보다 작고 old 종료는 new 시작보다 큰 경우

        TakeClass takeClass = takeClassRepository.save(
                TakeClass.builder()
                    .user(user)
                    .classes(classes)
                    .build());

        user.registration(takeClass);


       // classes.registration();
        classes.SetCurStudent(takeClassRepository.findNumOfCurStudent(classes.getClassId()) );

        UserResponseDto userResponseDto =userService.findById(userId);// .. 기본값 10L 문제 처리해야..
        //+ 평균 구하는게 소수점 반영 x
        UserUpdateRequestDto userUpdateRequestDto=new UserUpdateRequestDto(userResponseDto);
        userService.update(userId, userUpdateRequestDto);


        return takeClass.getTakeId();
    }

    @Transactional
    public void delete(Long takeId) {
        TakeClass takeClass = takeClassRepository.findById(takeId).get();

        if( ! takeClassRepository.findNoAgain(takeClass.getUser().getUserId(),takeClass.getClasses().getClassId()).isEmpty() )
            throw new IllegalArgumentException("Failed: above B0 cannot erase classes");

        User user = userRepository.findById(takeClass.getUser().getUserId()).get();

        user.cancel(takeClass);

        Classes classes = classesRepository.findById(takeClass.getClasses().getClassId()).get();


       // classes.cancel();
        classes.SetCurStudent(takeClassRepository.findNumOfCurStudent(classes.getClassId()) );




        takeClassRepository.delete(takeClass);

        UserResponseDto userResponseDto =userService.findById(user.getUserId());// .. 기본값 10L 문제 처리해야..
        //+ 평균 구하는게 소수점 반영 x
        UserUpdateRequestDto userUpdateRequestDto=new UserUpdateRequestDto(userResponseDto);
        userService.update(takeClass.getUser().getUserId(), userUpdateRequestDto);

    }

    @Transactional
    public List<TakeClass> findEndClass(Long userId){
        return takeClassRepository.findEndClass(userId);

    }

    @Transactional
    public List<TakeClass> findNotEndClass(Long userId){
        return takeClassRepository.findNotEndClass(userId);

    }

    @Transactional
    public List<TakeClass> findNotDoneClass(Long userId){
        return takeClassRepository.findNotDoneClass(userId);

    }


    @Transactional
    public Long update(Long takeId, TakeClassUpdateRequestDto takenrequestDto) {
        TakeClass takeClass = takeClassRepository.findById(takeId)
                .orElseThrow(() -> new IllegalArgumentException("Failed: No Class Info"));





        //여기서 새로 추가된 take class 포함한 평균학점 계산하기.




        //.orElseThrow(() -> new UsernameNotFoundException("Failed: No User Info"));


        takeClass.update(takenrequestDto);
/*
        UserResponseDto userResponseDto =userService.findById(takenrequestDto.getUserId());
        UserUpdateRequestDto userUpdateRequestDto=new UserUpdateRequestDto(userResponseDto);
        userService.update(takenrequestDto.getUserId(),userUpdateRequestDto);
*/
        return takeId;
    }



    @Transactional
    public TakeClassUpdateRequestDto findbyId(Long takeID){  //take class에 정보 업데이트를 위해 생성
        //takeId로 찾고있는 takeclass를 찾아 Grade를 찾아 학생이 take한 수업의 grade정보를 가져오거나 변경가능.
        TakeClass takeClass= takeClassRepository.findById(takeID)
                .orElseThrow(() -> new IllegalArgumentException("Failed: No TakeClass Info"));

        return new TakeClassUpdateRequestDto(takeID,takeClass.getGrade()
                ,takeClass.getUser().getUserId());
    }

    @Transactional
    public List<TakeClass> findByDay(Long userId, Long day){
       return takeClassRepository.findByDay(userId, day);
               // .orElseThrow(() -> new IllegalArgumentException("Failed: No TakeClass Info"));
    }




}
