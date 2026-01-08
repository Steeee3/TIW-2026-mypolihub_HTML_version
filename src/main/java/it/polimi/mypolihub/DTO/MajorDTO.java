package it.polimi.mypolihub.DTO;

import it.polimi.mypolihub.entity.Major;

public class MajorDTO {
    private String name;

    public MajorDTO(Major major) {
        name = major.getName();
    }

    public String getName() {
        return name;
    }
}
