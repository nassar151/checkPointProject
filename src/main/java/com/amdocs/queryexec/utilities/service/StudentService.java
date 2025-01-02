// src/main/java/com/amdocs/queryexec/service/UserService.java
package com.amdocs.queryexec.utilities.service;

import com.amdocs.queryexec.utilities.exception.StudentRegistrationException;
import com.amdocs.queryexec.utilities.model.Course;
import com.amdocs.queryexec.utilities.model.Enrollment;
import com.amdocs.queryexec.utilities.model.Student;
import com.amdocs.queryexec.utilities.repository.CourseRepository;
import com.amdocs.queryexec.utilities.repository.EnrollmentRepository;
import com.amdocs.queryexec.utilities.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private StudentRepository studentRepository;
    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    private Map<String, String> studentSessions = new HashMap<>();

    public Student createStudent(Student student) {
        logger.info("Creating a new student with name: {}", student.getName());

        // Check if the student already exists
        boolean studentExists = studentRepository.findAll().stream()
                .anyMatch(existingStudent -> existingStudent.getName().equals(student.getName()));
        if (studentExists) {
            logger.warn("Student with name: {} already exists", student.getName());
            throw new StudentRegistrationException("Student with name " + student.getName() + " already exists");
        }

        // Generate a unique specialKey
        String specialKey = UUID.randomUUID().toString();
        student.setSpecialKey(specialKey);
        logger.debug("Generated specialKey: {}", specialKey);

        // Save the student to the repository
        Student savedStudent = studentRepository.save(student);
        logger.info("Student saved with ID: {}", savedStudent.getId());

        // Return the student along with the special key
        return savedStudent;
    }

    public void deleteStudent(Long studentId) {
        logger.info("Deleting student with ID: {}", studentId);
        studentRepository.deleteById(studentId);
        logger.info("Student deleted successfully with ID: {}", studentId);
    }

    public Student findBySpecialKey(String specialKey) {
        logger.info("Finding student with special key: {}", specialKey);
        Student student = studentRepository.findAll().stream()
                .filter(s -> specialKey.equals(s.getSpecialKey()))
                .findFirst()
                .orElse(null);
        if (student != null) {
            logger.info("Student found with special key: {}", specialKey);
        } else {
            logger.warn("No student found with special key: {}", specialKey);
        }
        return student;
    }

    public String createSession(Student student) {
        logger.info("Creating session for student with ID: {}", student.getId());
        String sessionKey = UUID.randomUUID().toString();
        studentSessions.put(sessionKey, student.getId().toString());
        logger.info("Validating session with session keysuccessfullysuccessfullysuccessfully: {}", studentSessions);

        logger.info("Session created successfully with session key: {}", sessionKey);
        return sessionKey;
    }


    public String validateSession(String sessionKey) {
        logger.info("Validating session with session key: {}", sessionKey);
        logger.info("Validating session with session key: {}", studentSessions);
        String studentId = studentSessions.get(sessionKey);
        if (studentId != null) {
            logger.info("Session validated successfully for session key: {}", sessionKey);
        } else {
            logger.warn("Invalid session key: {}", sessionKey);
        }
        return studentId;
    }

    public boolean registerStudentToCourse(Long studentId, Long courseId) {
        logger.info("Registering student with ID: {} to course with ID: {}", studentId, courseId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.error("Student not found with ID: {}", studentId);
                    throw new StudentRegistrationException("Student not found with id :{}");});


        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    logger.error("Course not found with ID: {}", courseId);
                    throw new StudentRegistrationException("Course not found with id ");
                });

        long registeredCourses = enrollmentRepository.countByStudentId(studentId);
        if (registeredCourses >= 2) {
            logger.warn("Student with ID: {} is already registered for 2 courses", studentId);
            throw new StudentRegistrationException("Student  is already registered for 2 courses");
        }

        long courseRegistrations = enrollmentRepository.countByCourseId(courseId);
        if (courseRegistrations >= 30) {
            logger.warn("Course with ID: {} already has 30 students", courseId);
            throw new StudentRegistrationException("Course {} already has 30 students");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setRecord(null);

        enrollmentRepository.save(enrollment);
        logger.info("Student with ID: {} registered to course with ID: {} successfully", studentId, courseId);
        return true;
    }

    public boolean cancelRegistration(Long studentId, Long courseId) {
        logger.info("Cancelling registration for student with ID: {} from course with ID: {}", studentId, courseId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.error("Student not found with ID: {}", studentId);
                    return new RuntimeException("Student not found");
                });

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    logger.error("Course not found with ID: {}", courseId);
                    return new RuntimeException("Course not found");
                });

        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (enrollment != null) {
            enrollmentRepository.delete(enrollment);
            logger.info("Registration cancelled successfully for student with ID: {} from course with ID: {}", studentId, courseId);
            return true;
        } else {
            logger.warn("No registration found for student with ID: {} in course with ID: {}", studentId, courseId);
            return false;
        }
    }

    // DTO class to hold the student along with the session key
    public static class StudentWithSession {
        private Student student;
        private String sessionKey;

        public StudentWithSession(Student student, String sessionKey) {
            this.student = student;
            this.sessionKey = sessionKey;
        }

        public Student getStudent() {
            return student;
        }

        public void setStudent(Student student) {
            this.student = student;
        }

        public String getSessionKey() {
            return sessionKey;
        }

        public void setSessionKey(String sessionKey) {
            this.sessionKey = sessionKey;
        }
    }

    // Fetch all students along with their enrolled courses
    public List<StudentWithCoursesDTO> getAllStudentsWithCourses() {
        logger.info("Fetching all students with their enrolled courses");
        List<Student> students = studentRepository.findAll();
        List<StudentWithCoursesDTO> studentsWithCourses = students.stream().map(student -> {
            List<Course> courses = enrollmentRepository.findByStudentId(student.getId()).stream()
                    .map(Enrollment::getCourse)
                    .collect(Collectors.toList());
            return new StudentWithCoursesDTO(student, courses);
        }).collect(Collectors.toList());
        logger.info("Fetched {} students with their enrolled courses", studentsWithCourses.size());
        return studentsWithCourses;
    }

    // DTO class to hold the student along with their courses
    public static class StudentWithCoursesDTO {
        private Student student;
        private List<Course> courses;

        public StudentWithCoursesDTO(Student student, List<Course> courses) {
            this.student = student;
            this.courses = courses;
        }

        public Student getStudent() {
            return student;
        }

        public void setStudent(Student student) {
            this.student = student;
        }

        public List<Course> getCourses() {
            return courses;
        }

        public void setCourses(List<Course> courses) {
            this.courses = courses;
        }
    }
}