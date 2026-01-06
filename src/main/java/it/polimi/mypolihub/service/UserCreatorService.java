package it.polimi.mypolihub.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.polimi.mypolihub.DTO.UserImportReportDTO;
import it.polimi.mypolihub.entity.Role;
import it.polimi.mypolihub.entity.User;
import it.polimi.mypolihub.repository.UserRepository;

@Service
public class UserCreatorService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private record Name(String name, String surname) { }

    public UserImportReportDTO importUsersFromUpload(MultipartFile file, Role role, String defaultPassword) {
        try (BufferedReader br = bufferedReaderOfFile(file)) {

            return insertUsersWithSameRoleAndDefaultPassword(br, role, defaultPassword);

        } catch (IOException e) {
            throw new RuntimeException("Upload file error", e);
        }
    }

    public UserImportReportDTO importUsersFromFile(String fileName, Role role, String defaultPassword) {
        var file = new ClassPathResource(fileName);

        try (BufferedReader br = bufferedReaderOfFile(file)) {

            return insertUsersWithSameRoleAndDefaultPassword(br, role, defaultPassword);

        } catch (IOException e) {
            throw new RuntimeException("File error " + file.getPath(), e);
        }
    }

    private BufferedReader bufferedReaderOfFile(ClassPathResource file) throws IOException {
        return new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
    }

    private BufferedReader bufferedReaderOfFile(MultipartFile file) throws IOException {
        return new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
    }

    private UserImportReportDTO insertUsersWithSameRoleAndDefaultPassword(BufferedReader br, Role role, String defaultPassword) throws IOException {
        UserImportReportDTO report = new UserImportReportDTO();

        String readName;
        while ((readName = br.readLine()) != null) {
            readName = readName.trim();

            if (readName.isBlank()) {
                report.incSkipped();
                continue;
            }
                
            Name fullName = getNameFromRawName(readName);
            if (fullName == null) {
                report.incSkipped();
                continue;
            }

            User user = buildUser(fullName.name, fullName.surname, role);
            userRepository.save(user);
            report.incCreated();
        }

        return report;
    }

    private Name getNameFromRawName(String rawName) {
        try {
            List<String> nameAndSurname = getNameAndSurnameFromFullName(rawName);

            return new Name(nameAndSurname.getFirst(), nameAndSurname.getLast());

        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());

            return null;
        }
    }

    private List<String> getNameAndSurnameFromFullName(String fullName) {
        int firstSpace = fullName.indexOf(" ");

        if (firstSpace == -1) {
            throw new IllegalArgumentException("Name and surname must be separated with a space: " + fullName);
        }

        String name = fullName.substring(0, firstSpace);
        name = capitalizeAndSanify(name);

        String surname = fullName.substring(firstSpace + 1);
        surname = capitalizeAndSanify(surname);

        return List.of(name, surname);
    }

    private String capitalizeAndSanify(String word) {
        word = word.trim().
            replaceAll("\\s+", " ")
            .toLowerCase(Locale.ROOT);

        if (word.isEmpty()) {
            return word;
        }

        int spaceIndex = word.indexOf(" ");
        while (spaceIndex != -1) {
            char c = word.charAt(spaceIndex + 1);

            c = Character.toUpperCase(c);
            word = word.substring(0, spaceIndex + 1) + c + word.substring(spaceIndex + 2);

            spaceIndex = word.indexOf(" ", spaceIndex + 1);
        }

        char firstLetter = word.charAt(0);
        firstLetter = Character.toUpperCase(firstLetter);
        word = firstLetter + word.substring(1);

        return word;
    }

    private User buildUser(String name, String surname, Role role) {
        User u = new User();
        String password = "password";

        u.setName(name);
        u.setSurname(surname);
        u.setEmail(createUniqueEmail(name + " " + surname));
        u.setPassword(passwordEncoder.encode(password));
        u.setRole(role);

        return u;
    }

    private String createUniqueEmail(String fullName) {
        String base = fullName
            .toLowerCase()
            .replace(" ", ".");
        String domain = "@mail.polimi.it";
        String email = base + domain;

        int i = 2;
        while ((userRepository.existsByEmail(email))) {
            email = base + i + domain;
            i++;
        }
        return email;
    }
}
