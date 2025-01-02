package com.amdocs.queryexec.utilities.controller;


import com.amdocs.queryexec.utilities.model.Student;
import com.amdocs.queryexec.utilities.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/studentLogin")
    public ResponseEntity<Map<String, String>> studentLogin(@RequestParam String specialKey) {
        Student student = studentService.findBySpecialKey(specialKey);
        if (student != null) {
            String sessionKey = studentService.createSession(student);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Successfully logged in");
            response.put("sessionKey", sessionKey);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(Collections.singletonMap("error", "Invalid special key"));
        }
    }

}