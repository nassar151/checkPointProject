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
import java.util.Map;

@Service
public class EnrollmentService {


    Logger logger = LoggerFactory.getLogger(EnrollmentService.class);

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    public Map<String, String> getEnrollment(Long studentId, Long courseId) {
        Logger logger = LoggerFactory.getLogger(EnrollmentService.class);
        logger.info("Starting getEnrollment with studentId: {}, courseId: {}", studentId, courseId);

        // Fetch the student and course entities
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.error("Student not found with ID: {}", studentId);
                    throw  new StudentRegistrationException("Student not found  ");
                });
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    logger.error("Course not found with ID: {}", courseId);
                    throw new StudentRegistrationException("Course not found  ");
                });

        // Fetch the enrollment based on studentId and courseId
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);

        if (enrollment != null) {
            // If enrollment exists, return the record
            logger.info("Enrollment found, returning record");
            Map<String, String> response = new HashMap<>();
            String record = enrollment.getRecord() != null ? enrollment.getRecord().toString() : "0";
            response.put("record", record);
            response.put(enrollment.getCourse().getCourseName(), enrollment.getCourse().getId().toString());
            logger.info("Returning response: {}", response);
            return response;
        } else {
            logger.error("Enrollment not found for studentId: {} and courseId: {}", studentId, courseId);
            throw new StudentRegistrationException("student not regestrid to this course");
        }
    }

    public Map<String, String> addOrUpdateEnrollment(Long studentId, Long courseId, String record) {
        logger.info("Starting addOrUpdateEnrollment with studentId: {}, courseId: {}, record: {}", studentId, courseId, record);

        // Fetch the student and course entities
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.error("Student not found with ID: {}", studentId);
                    throw  new StudentRegistrationException("Student not found ");
                });
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    logger.error("Course not found with ID: {}", courseId);
                    throw new StudentRegistrationException("Course not found ");
                });

        // Check if the enrollment exists
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);

        if (enrollment == null) {
            // Create a new enrollment if not found
            logger.info("Enrollment not found, creating new enrollment");
            enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setCourse(course);
            enrollment.setRecord(record);
        } else {
            // Update the record if enrollment exists
            logger.info("Enrollment found, updating record");
            enrollment.setRecord(record);
        }

        // Save the enrollment to the database
        enrollmentRepository.save(enrollment);
        logger.info("Enrollment saved successfully");

        Map<String, String> response = new HashMap<>();
        response.put("record", enrollment.getRecord().toString());
        response.put(enrollment.getCourse().getCourseName(), enrollment.getCourse().getId().toString());
        logger.info("Returning response: {}", response);

        return response;
    }

    public void deleteEnrollment (Long studentId, Long courseId){
            // Fetch the enrollment based on studentId and courseId
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.error("Student not found with ID: {}", studentId);
                    throw  new StudentRegistrationException("Student not found  ");
                });
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    logger.error("Course not found with ID: {}", courseId);
                    throw  new StudentRegistrationException("Course not found ");
                });
            Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);

            if (enrollment != null) {
                // If enrollment exists, delete it
                enrollmentRepository.delete(enrollment);
            } else {
                throw new StudentRegistrationException("student with id "+ studentId +" not regestrid to this course " + courseId);

            }
        }




}