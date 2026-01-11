package it.polimi.mypolihub.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.polimi.mypolihub.DTO.ResultDTO;
import it.polimi.mypolihub.repository.ResultRepository;

@Service
public class ResultService {
    
    @Autowired
    private ResultRepository resultRepository;

    @Transactional(readOnly = true)
    public List<ResultDTO> getAllResults() {
        return resultRepository.findAllByOrderByIdAsc().stream()
            .map(r -> new ResultDTO(r))
            .toList();
    }
}
