package com.school.project.responseEntity;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.school.project.entity.Student;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class StudentResponse {
	
	@JsonProperty("Roll Number")
	private Long roll_number;
	
	private Long age;
	
	private String className;
	
	private Date DoB;
	
	public StudentResponse(Student student) {
		this.roll_number=student.getRollNo();
		this.age=student.getAge();
		this.className=student.getClassName();
		this.DoB=student.getDOB();
	}

}
