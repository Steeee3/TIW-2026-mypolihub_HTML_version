package it.polimi.mypolihub.DTO;

import java.util.HashSet;
import java.util.Set;

import it.polimi.mypolihub.entity.Course;
import it.polimi.mypolihub.entity.Student;
import it.polimi.mypolihub.entity.User;

public class CourseDTO {
    private String name;
    private Integer cfu;
    private MajorDTO major;
    private ProfessorDTO professor;
    private Set<StudentDTO> students;

    public CourseDTO(Course course) {
        name = course.getName();
        cfu = course.getCfu();
        
        major = new MajorDTO(course.getMajor());

        User professorUserData = course.getProfessor().getUser();
        professor = new ProfessorDTO(professorUserData);

        students = new HashSet<>();
        for (Student s : course.getStudents()) {
            User studentUserData = s.getUser();

            students.add(new StudentDTO(studentUserData, s));
        }
    }

    public String getName() {
        return name;
    }

    public Integer getCfu() {
        return cfu;
    }

    public MajorDTO getMajor() {
        return major;
    }

    public ProfessorDTO getProfessor() {
        return professor;
    }

    public Set<StudentDTO> getStudents() {
        return students;
    }
}
