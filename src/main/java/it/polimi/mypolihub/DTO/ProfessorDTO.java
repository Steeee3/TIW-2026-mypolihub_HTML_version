package it.polimi.mypolihub.DTO;

import it.polimi.mypolihub.entity.User;

public class ProfessorDTO {
    private String name;
    private String surname;
    private String email;

    public ProfessorDTO(User professor) {
        name = professor.getName();
        surname = professor.getSurname();
        email = professor.getEmail();
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
}
