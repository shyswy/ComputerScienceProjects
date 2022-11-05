package com.wadekang.toyproject.courseregistrationsystem.controller;

import com.wadekang.toyproject.courseregistrationsystem.controller.dto.ClassSearch;
import com.wadekang.toyproject.courseregistrationsystem.controller.dto.ClassesResponseDto;
import com.wadekang.toyproject.courseregistrationsystem.domain.Classes;
import com.wadekang.toyproject.courseregistrationsystem.domain.Course;
import com.wadekang.toyproject.courseregistrationsystem.domain.Major;
import com.wadekang.toyproject.courseregistrationsystem.domain.User;
import com.wadekang.toyproject.courseregistrationsystem.service.ClassesService;
import com.wadekang.toyproject.courseregistrationsystem.service.CourseService;
import com.wadekang.toyproject.courseregistrationsystem.service.MajorService;
import lombok.RequiredArgsConstructor;
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
public class CourseController {

    private final CourseService courseService;
    private final MajorService majorService;

    private final ClassesService classesService;

    @GetMapping("/courses")
    public String courseList(@ModelAttribute("classSearch") ClassSearch classSearch, Model model) {
        List<Course> courses = courseService.findByMajor(classSearch.getMajorId());
        //@ModelAttribute 어노테이션으로, html에서 만약 ClassSearch에 변화 생길 시, 이곳에도 반영되게
        List<Major> majors = majorService.findAll();
        List<Course> keywordCourses =courseService.findByMajorAndKeyword(classSearch.getMajorId(),classSearch.getKeyword());

        model.addAttribute("courses", keywordCourses);//html에서 Classsearch가 바뀌면, 그값을 다시 html에 반영해준다.
        model.addAttribute("majors", majors);

        return "courseList";
    }

    @GetMapping("/courses/{id}")
    public String classList(@PathVariable("id") Long courseId, Model model) {
        Course course = courseService.findById(courseId);

        model.addAttribute("classes", course.getClasses());

        return "classList";
    }

    /*
    @GetMapping("/search")
    public String search(@RequestParam(value="keyword") Long keyword,Model model){
        ClassesResponseDto classes =classesService.findById(keyword);
        List<ClassesResponseDto> classes=classesService.findById(keyword);
    }

     */





    @GetMapping("/admincourses")
    public String admincourseList(@ModelAttribute("classSearch") ClassSearch classSearch, Model model) {
        List<Course> courses = courseService.findByMajor(classSearch.getMajorId());
        List<Major> majors = majorService.findAll();

        model.addAttribute("courses", courses);
        model.addAttribute("majors", majors);

        return "adminCourseList";
    }

    @GetMapping("/admincourses/{id}")
    public String adminclassList(@PathVariable("id") Long courseId, Model model) {
        Course course = courseService.findById(courseId);

        model.addAttribute("classes", course.getClasses());

        return "adminClassList";
    }









}
