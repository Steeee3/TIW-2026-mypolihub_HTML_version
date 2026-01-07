package it.polimi.mypolihub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.polimi.mypolihub.entity.Major;
import it.polimi.mypolihub.repository.MajorRepository;

@Service
public class MajorService {

    @Autowired
    private MajorRepository majorRepository;

    @Transactional
    public void createMajor(String rawName) {
        String name = rawName == null ? "" : rawName.trim().replaceAll("\\s+", " ");
        
        if (name.isBlank()) throw new IllegalArgumentException("Major name is blank");

        if (majorRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Major already exists: " + name);
        }

        Major m = new Major();
        m.setName(name);
        majorRepository.save(m);
    }
}
