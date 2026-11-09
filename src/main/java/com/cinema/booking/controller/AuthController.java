package com.cinema.booking.controller;

import com.cinema.booking.model.User;
import com.cinema.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ===== ĐĂNG KÝ =====
    @GetMapping("/register")
    public String showRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Email này đã được đăng ký.");
            return "register";
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp.");
            return "register";
        }
        if (password.length() < 6) {
            model.addAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự.");
            return "register";
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(User.Role.customer);
        user.setIsActive(true);
        userRepository.save(user);

        return "redirect:/login?registered=true";
    }

    // ===== ĐĂNG NHẬP =====
    @GetMapping("/login")
    public String showLogin(
            @RequestParam(required = false) String registered,
            @RequestParam(required = false) String error,
            Model model) {

        if ("true".equals(registered))
            model.addAttribute("success", "Đăng ký thành công! Hãy đăng nhập.");
        if (error != null)
            model.addAttribute("error", "Email hoặc mật khẩu không đúng.");
        return "login";
    }
}