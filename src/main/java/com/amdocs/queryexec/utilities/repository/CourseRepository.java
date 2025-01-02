package com.amdocs.queryexec.utilities.repository;

import com.amdocs.queryexec.utilities.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}