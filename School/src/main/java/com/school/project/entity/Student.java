package com.school.project.entity;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name="student_details")
public class Student {
	@Id
	@Column(name="roll_number")
	private Long RollNo;
	
	@Column(name="age")
	private Long age;
	
	@Column(name="class")
	private String ClassName;
	
	@Column(name="date_of_birth")
	private Date DOB;

}
