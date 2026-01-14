package it.polimi.mypolihub.DTO;

import it.polimi.mypolihub.entity.Registration;

public class RegistrationDTO {
    private Integer id;
    private StudentDTO student;
    private String status;
    private ExamDTO exam;
    private ResultDTO result;

    public RegistrationDTO(Registration registration) {
        id = registration.getId();
        student = new StudentDTO(registration.getStudent());
        status = registration.getStatus().getValue();

        exam = new ExamDTO(registration.getExam());
        result = new ResultDTO(registration.getResult());
    }

    public Integer getId() {
        return id;
    }

    public StudentDTO getStudent() {
        return student;
    }

    public String getStatus() {
        return status;
    }

    public ExamDTO getExam() {
        return exam;
    }

    public ResultDTO getResult() {
        return result;
    }
}
