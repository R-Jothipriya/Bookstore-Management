package com.bookStore.bookStore.controller;

import com.bookStore.bookStore.entity.User;
import com.bookStore.bookStore.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // Show login form
    @GetMapping("/login")
    public String loginForm(HttpSession session, Model model) {
        String loginError = (String) session.getAttribute("loginError");
        if (loginError != null) {
            model.addAttribute("loginError", loginError);
            session.removeAttribute("loginError");
        }
        return "login";
    }

    // Handle login form submission
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session) {

        // Fixed Admin Login
        if (email.equals("admin@example.com") && password.equals("admin123")) {
            User admin = new User();
            admin.setEmail(email);
            admin.setPassword(password);
            admin.setRole("ADMIN");
            admin.setName("Admin");
            session.setAttribute("user", admin);
            return "redirect:/available_books";
        }

        // Check if user exists in DB
        User user = userRepository.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            // Existing user login
            session.setAttribute("user", user);
            return "redirect:/available_books";
        }

        // If user doesn't exist, create and save dynamically as USER
        if (user == null) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setRole("USER");
            newUser.setName("User");

            userRepository.save(newUser);
            session.setAttribute("user", newUser);
            return "redirect:/available_books";
        }

        // Wrong password
        session.setAttribute("loginError", "Invalid credentials");
        return "redirect:/login";
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
