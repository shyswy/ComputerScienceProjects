package com.wadekang.toyproject.courseregistrationsystem.service;

import com.wadekang.toyproject.courseregistrationsystem.controller.dto.UserResponseDto;
import com.wadekang.toyproject.courseregistrationsystem.controller.dto.UserSignUpDto;
import com.wadekang.toyproject.courseregistrationsystem.controller.dto.UserUpdateRequestDto;
import com.wadekang.toyproject.courseregistrationsystem.domain.Classes;
import com.wadekang.toyproject.courseregistrationsystem.domain.User;
import com.wadekang.toyproject.courseregistrationsystem.repository.MajorRepository;
import com.wadekang.toyproject.courseregistrationsystem.repository.TakeClassRepository;
import com.wadekang.toyproject.courseregistrationsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final MajorRepository majorRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final TakeClassRepository takeClassRepository;

    @Transactional
        public Long join(UserSignUpDto signUpDto) {
            userRepository.findByLoginId(signUpDto.getLoginId())
                    .ifPresent(user -> {
                        throw new IllegalArgumentException("Failed: Already Exist ID!");
                    });

            if (!signUpDto.getPassword().equals(signUpDto.getPasswordConfirm())) {
                throw new IllegalArgumentException("Failed: Please Check Password!");
            }

            User user = User.signupBuilder()
                    .loginId(signUpDto.getLoginId())
                    .password(passwordEncoder.encode(signUpDto.getPassword()))
                    .username(signUpDto.getUsername())
                    .email(signUpDto.getEmail())
                    .phoneNumber(signUpDto.getPhoneNumber())
                    .major(majorRepository.findById(signUpDto.getMajorId()).get())
                    .build();
            return userRepository.save(user).getUserId();
    }

    public UserResponseDto findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Failed: No User Info"));

        return new UserResponseDto(user);
    }



    @Transactional
    public Long update(Long userId, UserUpdateRequestDto requestDto) {// 평점 평균도
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Failed: No User Info"));

        Long avg= takeClassRepository.findUserAverage(userId)
                .orElseThrow(() -> new IllegalArgumentException("Failed: no avgUserScore Info"));



        requestDto.setAverageScore(avg); //평균 점수 반영

        user.update(requestDto);

        return userId;
    }




    public List<User> findAll() {
        return userRepository.findAll();
    }
}
