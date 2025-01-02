package com.amdocs.queryexec.utilities.controller;

import com.amdocs.queryexec.utilities.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/register")
    public ResponseEntity<String> registerStudentToCourse(
            @RequestHeader("Session-Key") String sessionKey,
            @RequestParam Long courseId) {
        String studentId = studentService.validateSession(sessionKey);
        if (studentId == null) {
            return ResponseEntity.status(401).body("Invalid session key");
        }
        boolean isRegistered = studentService.registerStudentToCourse(Long.parseLong(studentId), courseId);

        if (isRegistered) {
            return ResponseEntity.ok("Student successfully registered to the course with correct session key.");
        } else {
            return ResponseEntity.badRequest().body("Registration failed. Check constraints.");
        }

    }

    @DeleteMapping("/cancelRegistration")
    public ResponseEntity<String> cancelRegistration(
            @RequestHeader("Session-Key") String sessionKey,
            @RequestParam Long courseId) {
        String studentId = studentService.validateSession(sessionKey);
        if (studentId == null) {
            return ResponseEntity.status(401).body("Invalid session key");
        }
        boolean isCancelled = studentService.cancelRegistration(Long.parseLong(studentId), courseId);

        if (isCancelled) {
            return ResponseEntity.ok("Student successfully unregistered from the course.");
        } else {
            return ResponseEntity.badRequest().body("Cancellation failed. Ensure the student is registered for the course.");
        }
    }
}