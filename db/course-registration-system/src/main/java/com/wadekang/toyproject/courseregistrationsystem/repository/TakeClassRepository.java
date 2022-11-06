package com.wadekang.toyproject.courseregistrationsystem.repository;

import com.wadekang.toyproject.courseregistrationsystem.domain.Classes;
import com.wadekang.toyproject.courseregistrationsystem.domain.Course;
import com.wadekang.toyproject.courseregistrationsystem.domain.TakeClass;
import com.wadekang.toyproject.courseregistrationsystem.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Optional;

public interface TakeClassRepository extends JpaRepository<TakeClass, Long> {

    //&& tc.grade!= 디폴트 값

    @Query("select avg(tc.grade) from TakeClass tc where  tc.user.userId = :userId and tc.grade <> 99L group by tc.user.userId")
    Optional<Long> findUserAverage(@Param("userId") Long userId); //"userId" 와 Long userId 이름 일치해야!!!!! 디폴트인 99일시 반영 x

    @Query("select distinct avg(tc.grade)   from TakeClass tc where tc.grade <> 99L and tc.classes.classId=:classId group by tc.classes.classId  ")
    Long  findClassAverage(@Param("classId") Long classId);

    @Query("select distinct avg(tc.user.averageScore)   from TakeClass tc where tc.grade <> 99L and tc.classes.classId=:classId group by tc.classes.classId  ")
    Long findClassUserAverage(@Param("classId") Long classId);

    //@Query(" select avg(tc.user.averageScore)   from TakeClass tc where tc.grade <> 99L and tc.classes.classId=:classId group by tc.classes.classId  ")
    //Optional<Long []> findClassAverage(@Param("classId") Long classId);


    @Query("select tc from TakeClass tc where tc.classes.startTime < :endTime and tc.classes.endTime > :startTime and tc.classes.Day= :Day and tc.user.userId =:userId")
    List<TakeClass> findRepeat(@Param("userId") Long userId, @Param("Day") Long Day,@Param("startTime") Long startTime, @Param("endTime") Long endTime);
    //studentId 같은거도 추가!
    //@Query("select c from Classes c where c.startTime < :endTime and c.endTime > :startTime and c.Day= :Day ")
    //List<Classes> findRepeat(@Param("Day") Long Day,@Param("startTime") Long startTime, @Param("endTime") Long endTime);
    // 시간대 중복 체크.  day 중복하고,   시작 or 종료 시간중 하나라도 기존 시작, 종료 시간 사이에 존재시 불가능!
    // old 시작이 new 종료보다 작고 old 종료는 new 시작보다 큰 경우
    //+ 같은 요일

    @Query("select tc from TakeClass tc where tc.user.userId = :userId and tc.classes.Day =:day order by tc.classes.startTime")
    List<TakeClass> findByDay(@Param("userId") Long userId ,@Param("day") Long day );


    //재수강 불가한 경우 찾기
    //학생, 수업 id로 기존에 해당 수업을 수강한 학생인지 찾고, 이전에 수강하였다면, 이전 grade가 B0 이상인거만 리턴 > 1개라도 있으면 재수강 불가 (99L은 학점 기입 안된 것이므로 재수강 가능.)
    @Query("select tc from TakeClass  tc where tc.user.userId =:userId and tc.classes.classId= :classId and tc.grade > 4L and tc.grade <> 99L")// 1= D0   8=A+   5=B0
    List<TakeClass> findNoAgain(@Param("userId") Long userId, @Param("classId") Long classId);






    //@Query("select tc.classes.classId    from TakeClass tc where tc.grade <> 99L and tc.classes.classId in" +
      //      "(select tc2.classes.classId from TakeClass tc2 where group by tc2.classes.classId order by  )")












}
