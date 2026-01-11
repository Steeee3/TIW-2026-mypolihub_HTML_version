package it.polimi.mypolihub.DTO;

import it.polimi.mypolihub.entity.Registration;

public class RegistrationDTO {
    private StudentDTO student;
    private String status;
    private String result;

    public RegistrationDTO(Registration registration) {
        student = new StudentDTO(registration.getStudent());
        status = registration.getStatus().getValue();
        result = registration.getResult().getValue();
    }

    public StudentDTO getStudent() {
        return student;
    }

    public String getStatus() {
        return status;
    }

    public String getResult() {
        return result;
    }
}
