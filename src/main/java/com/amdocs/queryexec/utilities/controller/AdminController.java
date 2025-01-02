package com.amdocs.queryexec.utilities.controller;

import com.amdocs.queryexec.utilities.model.Course;
import com.amdocs.queryexec.utilities.model.Student;
import com.amdocs.queryexec.utilities.service.CourseService;
import com.amdocs.queryexec.utilities.service.EnrollmentService;
import com.amdocs.queryexec.utilities.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private CourseService courseService;


    @PostMapping("/addStudents")
    public Map<String, String> addStudent(@RequestBody Student student) {
        Student createdStudent = studentService.createStudent(student);
        Map<String, String> response = new HashMap<>();
        response.put("studentId", student.getId().toString());
        response.put("specialKey", student.getSpecialKey());
        return response;
    }

    @DeleteMapping("/deleteStudent")
    public ResponseEntity<String> deleteStudent(@RequestParam Long studentId) {
        try {
            studentService.deleteStudent(studentId);
            return ResponseEntity.ok("Student deleted successfully with ID: " + studentId);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to delete: No student found with ID: " + studentId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/readRecord")
    public Map<String, String>  getRecord(@RequestParam Long studentId, @RequestParam Long courseId) {
        return enrollmentService.getEnrollment(studentId, courseId);
    }


    @PostMapping("/addRecord")
    public Map<String, String> addOrUpdateEnrollment(@RequestParam Long studentId,
                                            @RequestParam Long courseId,
                                            @RequestParam String record) {
        return enrollmentService.addOrUpdateEnrollment(studentId, courseId, record);
    }


    @DeleteMapping("/deleteRecord")
    public String deleteEnrollment(@RequestParam Long studentId, @RequestParam Long courseId) {
        try {
            enrollmentService.deleteEnrollment(studentId, courseId);
            return "Enrollment record deleted successfully for studentId: " + studentId + " and courseId: " + courseId;
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }


    @PostMapping("/courses")
    public Course createCourse(@RequestBody Course course) {
        return courseService.createCourse(course);
    }

    @DeleteMapping("/deleteCourse")
    public String deleteCourse(@RequestParam Long courseId) {
        try {
            courseService.deleteCourse(courseId);
            return "Course deleted successfully with ID: " + courseId;
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    @GetMapping("/allStudents")
    public List<StudentService.StudentWithCoursesDTO> getAllStudentsWithCourses() {
        return studentService.getAllStudentsWithCourses();
    }

    @GetMapping("/allCourses")
    public List<CourseService.CourseWithStudentsDTO> getAllCoursesWithStudents() {
        return courseService.getAllCoursesWithStudents();
    }






}