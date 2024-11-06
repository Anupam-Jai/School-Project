package com.school.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.school.project.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

}
