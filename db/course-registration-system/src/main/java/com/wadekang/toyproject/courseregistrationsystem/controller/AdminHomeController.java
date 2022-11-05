package com.wadekang.toyproject.courseregistrationsystem.controller;

import com.wadekang.toyproject.courseregistrationsystem.controller.dto.ClassUpdateRequestDto;

import com.wadekang.toyproject.courseregistrationsystem.domain.*;
import com.wadekang.toyproject.courseregistrationsystem.service.ClassesService;
import com.wadekang.toyproject.courseregistrationsystem.service.CourseService;
import com.wadekang.toyproject.courseregistrationsystem.service.MajorService;
import com.wadekang.toyproject.courseregistrationsystem.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminHomeController {


    private final HttpSession httpSession;
    private final MajorService majorService;

    private final CourseService courseService;
    private final ClassesService classesService;

    private final RoomService roomService;

    @GetMapping(value = { "/adminHome"})
    public String home(Model model) {
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User us=(User) user;
        log.info("{}", user);
        if (user != "anonymousUser") {
            model.addAttribute("user", us);
        }

        if(!us.isAaaa())  ///관리자 아니면 home으로 다시 or 에러 문구 ( 문제: 로그아웃된 홈 화면으로)
            return "home";




        return "adminHome";
    }


    @GetMapping("/classescancel/{id}")
    public String courseCancel(@PathVariable("id") Long classId) {
        classesService.delete(classId);

        return "redirect:/admincourses?msg=Success!";
    }

/*
    @GetMapping("/classesregister/{id}")
    public String courseRegister(@PathVariable("id") Long classId) {
        try {
            classesService.save(classes);
        } catch (IllegalArgumentException e) {
            return "redirect:/register?msg=" + e.getMessage();
        }

        return "redirect:/register?msg=Success!";
    }

 */

    @GetMapping("/classesadd")
    public String signup(Model model,
                         @RequestParam(value="msg", required = false) String msg) {

        List<Course> courses = courseService.findAll();
        List<Room> rooms=roomService.findAll();
        model.addAttribute("classUpdateRequestDto", new ClassUpdateRequestDto());
        model.addAttribute("courses", courses);
        model.addAttribute("rooms",rooms);
        model.addAttribute("msg", msg);
        return "adminClassAdd";
    }
    @PostMapping("/classesadd")
    public String create(ClassUpdateRequestDto classUpdateRequestDto, Model model) {
        try {

            Long courseId= classUpdateRequestDto.getCourseId();// html에서 타임리프로 selected 받아오는건
            //객체를 온전히 받아오지 못한다.. 따라서 courseId 를 dto 항목에추가함.(html에서 select 된 course 객체를 받아오는게
            //아닌, 해당 course의 Id 만 가져온뒤, save에서 findbyid를 통해 해당 course 객체를 repository에서 가져옴

            Long roomId=classUpdateRequestDto.getRoomId(); //위와 같다.

            classesService.save(courseId, roomId,classUpdateRequestDto);
        } catch (Exception e) {
            List<Course> course = courseService.findAll();
            List<Room> room =roomService.findAll();

            model.addAttribute("courses", course);
            model.addAttribute("rooms",room);
            model.addAttribute("classUpdateRequestDto", classUpdateRequestDto);
            model.addAttribute("msg", e.getMessage());
            return "adminClassAdd";
        }

        return "redirect:/";
    }

    @GetMapping("/statistics")
    public String statistics(Model model){
        List<Classes> classes=classesService.findTopTen();



        int size=classes.size();
        if(size>10){
            size=10;
        }
        List<Classes> topTen=classes.subList(0,size);



        model.addAttribute("topTen",topTen);
        return "statistics";

    }


}
