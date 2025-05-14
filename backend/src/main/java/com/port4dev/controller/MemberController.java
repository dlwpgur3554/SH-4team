package com.port4dev.controller;

import com.port4dev.entity.Member;
import com.port4dev.repository.MemberRepository;
import com.port4dev.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*")
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private PostRepository postRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Member member) {
        Map<String, Object> response = new HashMap<>();
        
        // 아이디 중복 체크
        if (memberRepository.existsByUsername(member.getUsername())) {
            response.put("success", false);
            response.put("message", "이미 사용 중인 아이디입니다.");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 이메일 중복 체크
        if (memberRepository.existsByEmail(member.getEmail())) {
            response.put("success", false);
            response.put("message", "이미 사용 중인 이메일입니다.");
            return ResponseEntity.badRequest().body(response);
        }
        
        System.out.println("회원가입 - 원본 비밀번호: " + member.getPassword());  // 디버깅 로그
        
        // 비밀번호 암호화 후 저장
        String encodedPassword = passwordEncoder.encode(member.getPassword());
        System.out.println("회원가입 - 암호화된 비밀번호: " + encodedPassword);  // 디버깅 로그
        
        member.setPassword(encodedPassword);
        memberRepository.save(member);
        
        response.put("success", true);
        response.put("message", "회원가입이 완료되었습니다.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> response = new HashMap<>();
        
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        System.out.println("로그인 시도 - 아이디: " + username);  // 디버깅 로그
        
        if (username == null || password == null) {
            response.put("success", false);
            response.put("message", "아이디와 비밀번호를 모두 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }
        
        Member member = memberRepository.findByUsername(username);
        
        if (member == null) {
            response.put("success", false);
            response.put("message", "아이디가 존재하지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }
        
        System.out.println("DB에 저장된 비밀번호: " + member.getPassword());  // 디버깅 로그
        System.out.println("입력된 비밀번호: " + password);  // 디버깅 로그
        
        boolean matches = passwordEncoder.matches(password, member.getPassword());
        System.out.println("비밀번호 일치 여부: " + matches);  // 디버깅 로그
        
        if (!matches) {
            response.put("success", false);
            response.put("message", "비밀번호가 일치하지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 로그인 성공 시 민감한 정보 제외
        Map<String, Object> memberInfo = new HashMap<>();
        memberInfo.put("id", member.getId());
        memberInfo.put("username", member.getUsername());
        memberInfo.put("email", member.getEmail());
        memberInfo.put("name", member.getName());
        
        response.put("success", true);
        response.put("message", "로그인 성공");
        response.put("member", memberInfo);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> resetData) {
        Map<String, Object> response = new HashMap<>();
        
        String username = resetData.get("username");
        String newPassword = resetData.get("newPassword");
        
        if (username == null || newPassword == null) {
            response.put("success", false);
            response.put("message", "아이디와 새 비밀번호를 모두 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }
        
        Member member = memberRepository.findByUsername(username);
        
        if (member == null) {
            response.put("success", false);
            response.put("message", "아이디가 존재하지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 새 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(newPassword);
        member.setPassword(encodedPassword);
        memberRepository.save(member);
        
        response.put("success", true);
        response.put("message", "비밀번호가 재설정되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 내 정보 조회
    @GetMapping("/info")
    public ResponseEntity<?> getInfo(@RequestParam String username) {
        Member member = memberRepository.findByUsername(username);
        if (member == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "존재하지 않는 회원입니다."));
        }
        Map<String, Object> data = new HashMap<>();
        data.put("username", member.getUsername());
        data.put("email", member.getEmail());
        data.put("name", member.getName());
        return ResponseEntity.ok(data);
    }

    // 내 정보 수정
    @PostMapping("/update")
    public ResponseEntity<?> updateInfo(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String email = body.get("email");
        String name = body.get("name");
        String password = body.get("password");
        Member member = memberRepository.findByUsername(username);
        if (member == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "존재하지 않는 회원입니다."));
        }
        if (email != null && !email.isBlank()) {
            member.setEmail(email);
        }
        if (name != null && !name.isBlank()) {
            member.setName(name);
        }
        if (password != null && !password.isBlank()) {
            member.setPassword(passwordEncoder.encode(password));
        }
        memberRepository.save(member);
        return ResponseEntity.ok(Map.of("success", true, "message", "정보가 수정되었습니다."));
    }

    // 회원 탈퇴
    @Transactional
    @PostMapping("/delete")
    public ResponseEntity<?> deleteMember(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        Member member = memberRepository.findByUsername(username);
        if (member == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "존재하지 않는 회원입니다."));
        }
        if (password == null || !passwordEncoder.matches(password, member.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "비밀번호가 일치하지 않습니다."));
        }
        try {
            postRepository.deleteByAuthor(username);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "게시물 삭제 중 오류 발생: " + e.getMessage()));
        }
        try {
            memberRepository.delete(member);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "회원 삭제 중 오류 발생: " + e.getMessage()));
        }
        return ResponseEntity.ok(Map.of("success", true, "message", "회원 탈퇴가 완료되었습니다. 모든 게시물도 삭제되었습니다."));
    }
} 