package com.test.model.simple;

import java.util.ArrayList;
import java.util.List;

public class Course {

	private Long id;
	private String title;
	private List<Student> students = new ArrayList<Student>();
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<Student> getStudents() {
		return students;
	}
	public void setStudents(List<Student> students) {
		this.students = students;
	}
	
}
