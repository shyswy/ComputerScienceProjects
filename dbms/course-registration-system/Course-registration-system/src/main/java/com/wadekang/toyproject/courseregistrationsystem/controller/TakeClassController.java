package com.wadekang.toyproject.courseregistrationsystem.controller;

import com.wadekang.toyproject.courseregistrationsystem.controller.dto.ClassSearch;
import com.wadekang.toyproject.courseregistrationsystem.controller.dto.UserResponseDto;
import com.wadekang.toyproject.courseregistrationsystem.controller.dto.UserUpdateRequestDto;
import com.wadekang.toyproject.courseregistrationsystem.domain.Classes;
import com.wadekang.toyproject.courseregistrationsystem.domain.Course;
import com.wadekang.toyproject.courseregistrationsystem.domain.Major;
import com.wadekang.toyproject.courseregistrationsystem.domain.User;
import com.wadekang.toyproject.courseregistrationsystem.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TakeClassController {

    private final MajorService majorService;
    private final CourseService courseService;
    private final ClassesService classesService;
    private final TakeClassService takeClassService;

    private final HopeClassService hopeClassService;

    private final UserService userService;
    @GetMapping("/register") //수강 신청 클릭시 여기로 매핑
    public String courseRegistration(@ModelAttribute("classSearch") ClassSearch classSearch,
                                     @RequestParam(value  = "msg", required = false) String msg,
                                     Model model) {



        List<Course> courses = courseService.findByMajor(classSearch.getMajorId());
        List<Major> majors = majorService.findAll();
        List<Classes> keywordClasses =classesService.findByMajorAndKeyword(classSearch.getMajorId(),classSearch.getKeyword());
        List<Classes> classes = classesService.findByCourse(classSearch.getCourseId());
        model.addAttribute("classes", keywordClasses);
        model.addAttribute("courses", courses);
        model.addAttribute("majors", majors);
        model.addAttribute("msg", msg);

        return "courseRegistration";
    }

    @GetMapping("/register/{id}")
    public String courseRegister(@PathVariable("id") Long classId, @AuthenticationPrincipal User user) {
        try {

            //만약 이전에 이 수업을 들었고, B0 이상일시. save x
            takeClassService.save(user.getUserId(), classId);
           // UserResponseDto userResponseDto =userService.findById(user.getUserId());// .. 기본값 10L 문제 처리해야..
            //+ 평균 구하는게 소수점 반영 x
           // UserUpdateRequestDto userUpdateRequestDto=new UserUpdateRequestDto(userResponseDto);
            //userService.update(user.getUserId(),userUpdateRequestDto ); //유저



            

            //UserResponseDto userResponseDto=new UserResponseDto(user);
            //UserUpdateRequestDto userUpdateRequestDto=new UserUpdateRequestDto(userResponseDto);
            //userService.update(user.getUserId(),userUpdateRequestDto);


        } catch (IllegalArgumentException e) {
            return "redirect:/register?msg=" + e.getMessage();
        }

        return "redirect:/register?msg=Success!";
    }

    @GetMapping("/hopeRegister/{id}")
    public String hopeRegister(@PathVariable("id") Long classId, @AuthenticationPrincipal User user) {
        try {
            hopeClassService.save(user.getUserId(), classId);




        } catch (IllegalArgumentException e) {
            return "redirect:/register?msg=" + e.getMessage();
        }

        return "redirect:/register?msg=Success!";
    }

    @GetMapping("/cancel/{id}")
    public String courseCancel(@PathVariable("id") Long takeId) {
        try {
            takeClassService.delete(takeId);
        }
        catch (IllegalArgumentException e) {
            return "redirect:/myCourses?msg=" + e.getMessage();
        }

        return "redirect:/myCourses?msg=Success!";
    }

    @GetMapping("/hopeCancel/{id}")
    public String hopeCourseCancel(@PathVariable("id") Long hopeId) {
        hopeClassService.delete(hopeId);

        return "redirect:/myHope?msg=Success!";
    }


}
