package com.wadekang.toyproject.courseregistrationsystem.controller;

import com.wadekang.toyproject.courseregistrationsystem.controller.dto.*;
import com.wadekang.toyproject.courseregistrationsystem.domain.*;
import com.wadekang.toyproject.courseregistrationsystem.service.ClassesService;
import com.wadekang.toyproject.courseregistrationsystem.service.MajorService;
import com.wadekang.toyproject.courseregistrationsystem.service.TakeClassService;
import com.wadekang.toyproject.courseregistrationsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final MajorService majorService;

    private final ClassesService classesService;

    private final TakeClassService takeClassService;

    @GetMapping("/login")
    public String login(Model model,
                        @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "exception", required = false) String exception) {

        model.addAttribute("error", error);
        model.addAttribute("exception", exception);

        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model,
                         @RequestParam(value="msg", required = false) String msg) {
        List<Major> majors = majorService.findAll();
        model.addAttribute("signUpDto", new UserSignUpDto());
        model.addAttribute("majors", majors);
        model.addAttribute("msg", msg);
        return "signup";
    }

    @PostMapping("/signup")
    public String create(UserSignUpDto signUpDto, Model model) {
        try {
            userService.join(signUpDto);
        } catch (Exception e) {
            List<Major> majors = majorService.findAll();

            model.addAttribute("majors", majors);
            model.addAttribute("signUpDto", signUpDto);
            model.addAttribute("msg", e.getMessage());
            return "signup";
        }

        return "redirect:/";
    }

    @GetMapping("/myCourses")
    public String myCourseList(@AuthenticationPrincipal User user, Model model,
                               @RequestParam(value="msg", required = false) String msg) {
        UserResponseDto userResponseDto = userService.findById(user.getUserId());

        model.addAttribute("username", userResponseDto.getUsername());
        model.addAttribute("takeClasses", userResponseDto.getTakeClasses());
        model.addAttribute("msg", msg);

        return "myCourseList";
    }

    @GetMapping("/edit")
    public String update(@AuthenticationPrincipal User user, Model model, //AuthenticationPrincipal : 로그인 유저 객체 파라미터로받기.
                         @RequestParam(value="msg", required = false) String msg) {

        UserResponseDto userResponseDto = userService.findById(user.getUserId());
        model.addAttribute("userUpdateRequestDto", new UserUpdateRequestDto(userResponseDto));
        model.addAttribute("msg", msg);

        return "edit";
    }

    @PostMapping("/edit")
    public String userInfoUpdate(@AuthenticationPrincipal User user,
                                 UserUpdateRequestDto userUpdateRequestDto, Model model) {
        try {
            userService.update(user.getUserId(), userUpdateRequestDto);
        } catch (Exception e) {
            model.addAttribute("userUpdateRequestDto", userUpdateRequestDto);
            model.addAttribute("msg", e.getMessage());
            return "edit";
        }

        return "redirect:/";
    }




//requestParam: form에 input을 들어온 데이터 받아온다.
    // 처음 /admin1 으로 이곳으로 유도됨. admin1.html에 바꿀 classes의 dto를 파라미터로 전달해준다.
    @GetMapping("/admin1/{id}")   //html에서 class 정보 입력 받고 findbyId 로 id 찾아 여기로
    public String updateclasses( @AuthenticationPrincipal User user,@PathVariable("id") Long classId, Model model, //여기서 클래스 객체 존재 x
                         @RequestParam(value="msg", required = false) String msg) {
       if(!user.isAaaa())  ///관리자 아니면 home으로 다시 or 에러 문구 ( 문제: 로그아웃된 홈 화면으로)
           return "home";
       Long tmp=classId;
        ClassesResponseDto classesResponseDto = classesService.findById(classId); //여기에 정원 바꿀 class id 넣기!
        ClassUpdateRequestDto cur=new ClassUpdateRequestDto(classesResponseDto);
        cur.setClassId(classId);
        model.addAttribute("classUpdateRequestDto", cur);//savepoint1!!!!
        model.addAttribute("msg", msg);
        model.addAttribute("classId",tmp);
        return "admin1";
    }



    @PostMapping("/admin1/{id}")   //파라미터로 classid를 받아서 update.  action 비워두고 id로 포스트매핑 가능!
    public String classInfoUpdate(
@PathVariable("id") Long classId,
                                  ClassUpdateRequestDto classUpdateRequestDto, Model model
    ) {
        try {
            classesService.update(classId, classUpdateRequestDto);
        } catch (Exception e) {
            model.addAttribute("classUpdateRequestDto", classUpdateRequestDto);
            model.addAttribute("msg", e.getMessage());
            return "admin1";
        }
        return "redirect:/";
    }


    @GetMapping("/timetable")
    public String timetable(@AuthenticationPrincipal User user, Model model,
                               @RequestParam(value="msg", required = false) String msg) {
        UserResponseDto userResponseDto = userService.findById(user.getUserId());


        List<TakeClass> takeClasses = userResponseDto.getTakeClasses();



       // TakeClass takeClass1 = takeClasses.get(0);
       // credits.add(takeClass1.getClasses().getCredits().get(0));

        //Credit cc=userResponseDto.getCredits().get(0);

        model.addAttribute("username", userResponseDto.getUsername());
        model.addAttribute("takeClasses", userResponseDto.getTakeClasses());
        //model.addAttribute("credits",userResponseDto.getCredits()); //해당 학생이 들은 모든 수업.
        model.addAttribute("msg", msg);

        return "timeTable";
    }


    @GetMapping("/studentList")   //유저리스트 조회. 수업조회 클릭시 studentCLasses로 수업 내역 으로
    public String studentList( Model model,
                                     @RequestParam(value="msg", required = false) String msg) {

        List<User> users = userService.findAll();


        //user 찾기

        model.addAttribute("users", users);

        model.addAttribute("msg", msg);

        return "studentList";
    }

    @GetMapping("/studentClasses/{id}")  //해당 학생의 수업 내역 조회. 점수 변경 클릭시 해당 수업 점수 변경.
    public String studentCourseList( Model model,
                               @PathVariable("id") Long studentId,

                               @RequestParam(value="msg", required = false) String msg) {

        //user 찾기
        UserResponseDto userResponseDto = userService.findById(studentId);
        model.addAttribute("username", userResponseDto.getUsername());
        model.addAttribute("takeClasses", userResponseDto.getTakeClasses());
        model.addAttribute("msg", msg);
        model.addAttribute("userId",userResponseDto.getUserId());
        return "studentClassList";
    }


// @PathVariable Map<String,String> pathVars,   //여러 path variable 받아오기.
//String studentId=pathVars.get("studentId");
//  String classId=pathVars.get("classId");




    @GetMapping("/gradeupdate/{id}/{uid}/{cid}")   //html에서 class 정보 입력 받고 findbyId 로 id 찾아 여기로
    public String GradeUpdate(
            @PathVariable("id") Long takeId,
             @PathVariable("uid") Long userId,
            @PathVariable("cid") Long classId

            , Model model
    ) {
        TakeClassUpdateRequestDto takeClassUpdateRequestDto=takeClassService.findbyId(takeId);


       model.addAttribute("userId",takeClassUpdateRequestDto.getUserId());
        model.addAttribute("takeClassUpdateRequestDto", takeClassUpdateRequestDto);
        model.addAttribute("tid",takeId);
        model.addAttribute("userId",userId); //여기서 유저 아이디 model에 넣어줌
        model.addAttribute("classId",classId);

        return "changeGrade";
    }

    @PostMapping("/gradeupdate/{id}/{uid}/{cid}")   //파라미터로 classid를 받아서 update.  action 비워두고 id로 포스트매핑 가능!
    public String GradeUpdate(  //html에서 url에 유저 아이디와 takeid를 전달해준다.
           @PathVariable("id") Long takeId,
           @PathVariable("uid") Long userId,
          @PathVariable("cid") Long classId,
          // @ModelAttribute("userId") Long userId,
            TakeClassUpdateRequestDto takeClassUpdateRequestDto, Model model
    ) {
        try {

            takeClassService.update(takeId, takeClassUpdateRequestDto); //update는 grade정보만 변경가능!!!
            UserResponseDto userResponseDto =userService.findById(userId);// .. 기본값 10L 문제 처리해야..
            //+ 평균 구하는게 소수점 반영 x
            UserUpdateRequestDto userUpdateRequestDto=new UserUpdateRequestDto(userResponseDto);
            userService.update(userResponseDto.getUserId(),userUpdateRequestDto);

            ClassesResponseDto classesResponseDto=classesService.findById(classId);//?
            ClassUpdateRequestDto classUpdateRequestDto=new ClassUpdateRequestDto(classesResponseDto);
            classesService.update(classesResponseDto.getClassId(), classUpdateRequestDto);



        } catch (Exception e) {
            model.addAttribute("takeClassUpdateRequestDto", takeClassUpdateRequestDto);
            //model.addAttribute("msg", e.getMessage());
            return "changeGrade";
        }
        return "redirect:/studentList";
    }



}
