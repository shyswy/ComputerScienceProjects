package com.wadekang.toyproject.courseregistrationsystem.domain;

import com.wadekang.toyproject.courseregistrationsystem.controller.dto.UserUpdateRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class User extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column (nullable = false)
    private boolean aaaa;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @ManyToOne(targetEntity = Major.class, fetch = FetchType.LAZY)
    @JoinColumn(name="major_id")
    private Major major;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column
    private Long averageScore;

    @Column
    private String email;

    @Column
    private String phoneNumber;

    @Column
    private Long thisYearGrade;

    @Column
    private Long doneGrade;

    @OneToMany(mappedBy = "user")
    private List<TakeClass> takeClasses;

    @OneToMany(mappedBy = "user")
    private List<HopeClass> hopeClasses;

    @OneToMany(mappedBy = "user")
    private List<Credit> credits;

    @Builder(builderClassName = "UserSignUpBuilder", builderMethodName = "signupBuilder")
    public User(String loginId, String password, String username, Major major, String email, String phoneNumber,Long thisYearGrade
    ,Long doneGrade) {
        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.major = major;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = Role.STUDENT;


        this.thisYearGrade= 0L;
        this.doneGrade=0L;
        this.averageScore=0L;
        this.aaaa=false;
        this.takeClasses = new ArrayList<>();
        this.hopeClasses=new ArrayList<>();
        this.credits=new ArrayList<>();

    }

    public void update(UserUpdateRequestDto requestDto) {
        this.email = requestDto.getEmail();
        this.phoneNumber = requestDto.getPhoneNumber();
        this.averageScore=requestDto.getAverageScore();
        this.thisYearGrade=requestDto.getThisYearGrade();
        this.doneGrade=requestDto.getDoneGrade();

    }

    //== 수강 신청 ==//
    public void registration(TakeClass takeClass) {
        this.takeClasses.add(takeClass);
    }

    public void hopeRegistration(HopeClass hopeClass) {
        this.hopeClasses.add(hopeClass);
    }


    //== 수강 취소 ==//
    public void cancel(TakeClass takeClass) {
        this.takeClasses.remove(takeClass);
    }


    public void cancelHope(HopeClass hopeClass) {
        this.hopeClasses.remove(hopeClass);
    }



    /*
    public void hopeCancel(HopeClass hopeClass) {
        this.hopeClasses.remove(hopeClass);
    }
*/
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
