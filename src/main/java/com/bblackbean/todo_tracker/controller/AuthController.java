package com.bblackbean.todo_tracker.controller;

import com.bblackbean.todo_tracker.domain.User;
import com.bblackbean.todo_tracker.dto.UserRegisterRequest;
import com.bblackbean.todo_tracker.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 1) 회원가입 폼
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserRegisterRequest());
        return "register";
    }

    // 2) 회원가입 처리
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") UserRegisterRequest request, BindingResult result, Model model) {
        // @Valid : 해당 객체(request)에 지정된 유효성 검사(@NatBlank, @Size 등)를 실행
        // @ModelAttribute("user") : 폼 데이터를 바인딩하며, 뷰에서 폼에 사용된 name 속성과 매핑됨
        // BindingResult result : 폼 데이터의 유효성 검증 결과를 저장. 문제 발생 시 이 객체에 에러 정보가 추가됨
        
        // 비밀번호 확인 검사
        if ( !request.getPassword().equals(request.getConfirmPassword()) ) {
            result.rejectValue("confirmPassword", "password.mismatch", "비밀번호가 일치하지 않습니다.");
            // 비밀번호와 비밀번호 확인 값이 일치하지 않으면 BindingResult 객체에 에러를 추가
        }

        // 중복 사용자 검사
        if ( userRepository.findByUsername(request.getUsername()).isPresent() ) {
            result.rejectValue("username", "username.exists", "이미 사용 중인 아이디입니다.");
            // 입력된 아이디가 이미 데이터베이스에 존재하면 userRepository를 통해 확인 후 에러를 추가
        }

        // 에러 있으면 다시 회원가입 폼으로
        if ( result.hasErrors() ) {
            return "register";
        }

        // 정상 처리 : 비밀번호 암호화 후 저장
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))    // passwordEncoder.encode() 를 사용하여 보안 강화
                .roles(Set.of("ROLE_USER"))
                .build();
        userRepository.save(user);  // db에 데이터 저장

        return "redirect:/login";
    }
}
