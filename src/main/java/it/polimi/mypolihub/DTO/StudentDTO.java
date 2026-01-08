package it.polimi.mypolihub.DTO;

import it.polimi.mypolihub.entity.Student;
import it.polimi.mypolihub.entity.User;

public class StudentDTO {
    private String name;
    private String surname;
    private String email;
    private Integer number;
    private MajorDTO major;

    public StudentDTO(User userData, Student studentData) {
        name = userData.getName();
        surname = userData.getSurname();
        email = userData.getEmail();

        number = studentData.getNumber();
        major = new MajorDTO(studentData.getMajor());
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public Integer getNumber() {
        return number;
    }

    public MajorDTO getMajor() {
        return major;
    }
}
