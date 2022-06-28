package com.company.userapp.repository;

import com.company.userapp.exception.NotFoundException;
import com.company.userapp.model.Phone;
import com.company.userapp.model.User;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setup() {

        user = new User();
        user.setName("Juan Rodriguez");
        user.setPassword("hunter2");
        user.setEmail("juan@rodriguez.org");

        Phone phone = new Phone();
        phone.setNumber("1234567");
        phone.setCityCode("1");
        phone.setCountryCode("57");
        phone.setUser(user);
        user.setPhones(Set.of(phone));

        user.setCreated(Instant.now());
        user.setModified(Instant.now());
        user.setLastLogin(Instant.now());
        user.setActive(true);
        user.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");

        userRepository.save(user);

    }

    @Test
    public void whenUserIsSavedThenReturnUser() {

        Assertions.assertNotNull(user.getId(), "The user id cannot be null");
        Assertions.assertEquals(user.getName(), "Juan Rodriguez");
    }

    @Test
    public void whenFindUserByEmailThenReturnUser() {

        User _user = userRepository.findByEmail(user.getEmail());
        Assertions.assertEquals(user.getEmail(), _user.getEmail());
    }

    @Test
    public void whenSaveDuplicateEmailThenThrowsDataIntegrityViolationException() {
        User user = new User();
        user.setName("Juan Rodriguez");
        user.setPassword("hunter2");
        user.setEmail("juan@rodriguez.org");

        Phone phone = new Phone();
        phone.setNumber("1234567");
        phone.setCityCode("1");
        phone.setCountryCode("57");
        phone.setUser(user);
        user.setPhones(Set.of(phone));

        user.setCreated(Instant.now());
        user.setModified(Instant.now());
        user.setLastLogin(Instant.now());
        user.setActive(true);
        user.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(user);
        });
    }

    @Test
    public void whenSaveWithOutNameThenThrowsDataIntegrityViolationException() {

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            user.setName(null);
            userRepository.save(user);
        });
    }

    @Test
    public void whenSaveWithOutEmailThenThrowsDataIntegrityViolationException() {

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            user.setEmail(null);
            userRepository.save(user);
        });
    }

    @Test
    public void whenSaveWithOutPasswordThenThrowsDataIntegrityViolationException() {

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            user.setPassword(null);
            userRepository.save(user);
        });
    }

    @Test
    public void whenSaveWithOutCreatedDateThenThrowsDataIntegrityViolationException() {

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            user.setCreated(null);
            userRepository.save(user);
        });
    }

    @Test
    public void whenSaveWithOutModifiedDateThenThrowsDataIntegrityViolationException() {

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            user.setModified(null);
            userRepository.save(user);
        });
    }

    @Test
    public void whenSaveWithOutLastLoginThenThrowsDataIntegrityViolationException() {

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            user.setLastLogin(null);
            userRepository.save(user);
        });
    }

    @Test
    public void whenSaveWithOutActiveThenThrowsDataIntegrityViolationException() {

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            user.setActive(null);
            userRepository.save(user);
        });
    }

    @Test
    public void whenSaveWithOutTokenThenThrowsDataIntegrityViolationException() {

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            user.setToken(null);
            userRepository.save(user);
        });
    }

    @Test
    public void whenFindByIdThenReturnUser() {

        User found = userRepository.findById(user.getId()).orElseThrow(() -> {
            throw new NotFoundException("User not found");
        });

        Assertions.assertEquals(user.getId(), found.getId());
    }

    @Test
    public void whenFindByIdAndNotExistsThenThrowsNotFoundException() {

        Assertions.assertThrows(NotFoundException.class, () -> {
            UUID _id = UUID.randomUUID();
            userRepository.findById(_id).orElseThrow(() -> {
                throw new NotFoundException("User not found");
            });
        });
    }

    @Test
    public void whenFindAllThenReturnUsers() {

        List<User> users = userRepository.findAll();

        Assertions.assertEquals(Integer.valueOf(1), users.size());
        Assertions.assertTrue(!users.isEmpty());
    }

    @Test
    public void whenUpdateUserThenReturnUser() {

        user.setName("Juan Rodriguez Edit");

        userRepository.save(user);

        Assertions.assertEquals(user.getName(), "Juan Rodriguez Edit");
    }

    @AfterEach
    public void clean() {
        userRepository.deleteById(user.getId());
    }

}
