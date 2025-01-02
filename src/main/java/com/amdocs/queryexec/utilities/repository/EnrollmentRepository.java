package com.amdocs.queryexec.utilities.repository;

import com.amdocs.queryexec.utilities.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.course.id = :courseId")
    Enrollment findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    // Count enrollments by student ID
    long countByStudentId(Long studentId);

    // Count enrollments by course ID
    long countByCourseId(Long courseId);

}
