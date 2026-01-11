package it.polimi.mypolihub.DTO;

import it.polimi.mypolihub.entity.Result;

public class ResultDTO {
    private Integer id;
    private String value;

    public ResultDTO(Result result) {
        id = result.getId();
        value = result.getValue();
    }

    public Integer getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
