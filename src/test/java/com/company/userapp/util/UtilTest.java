package com.company.userapp.util;

import com.company.userapp.dto.model.PhoneDto;
import com.company.userapp.dto.model.UserDto;
import com.company.userapp.dto.response.UserResponse;
import com.company.userapp.model.Phone;
import com.company.userapp.model.User;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UtilTest {

    private UtilTest() {

    }


    public static UserDto buildUserDto() {

        UserDto userDto = new UserDto();
        userDto.setName("Juan Rodriguez");
        userDto.setEmail("juan@rodriguez.org");
        userDto.setPassword("hunter2");


        PhoneDto phoneDto = new PhoneDto("1234567", "1", "57");
        userDto.setPhones(Set.of(phoneDto));

        return userDto;
    }

    public static User buildUser(UserDto userDto) {

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());

        Set<Phone> phones = new HashSet<>();
        userDto.getPhones().forEach(p -> {
            phones.add(new Phone(1, p.getNumber(), p.getCityCode(), p.getCityCode(), user));
        });
        user.setPhones(phones);
        user.setCreated(Instant.now());
        user.setModified(Instant.now());
        user.setLastLogin(Instant.now());
        user.setActive(true);
        user.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");

        return user;
    }

    public static UserResponse buildUserResponse(User user, Set<PhoneDto> phones) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId().toString());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhones(phones);
        userResponse.setCreated(user.getCreated());
        userResponse.setModified(user.getModified());
        userResponse.setLastLogin(user.getLastLogin());
        userResponse.setIsActive(user.getActive());
        userResponse.setToken(user.getToken());

        return userResponse;
    }


}
