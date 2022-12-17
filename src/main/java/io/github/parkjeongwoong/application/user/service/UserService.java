package io.github.parkjeongwoong.application.user.service;

import io.github.parkjeongwoong.application.user.dto.UserSignupRequestDto;
import io.github.parkjeongwoong.application.user.repository.PasswordRepository;
import io.github.parkjeongwoong.application.user.repository.UserRepository;
import io.github.parkjeongwoong.entity.user.Password;
import io.github.parkjeongwoong.entity.user.User;
import io.github.parkjeongwoong.entity.user.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordRepository passwordRepository;

    @Transactional
    public String userSignup(UserSignupRequestDto requestDto) {
        try {
            userIdDuplicationCheck(requestDto.getUserId());
            return signup(requestDto.getUserId(), requestDto.getUserName(), requestDto.getUserEmail(), UserType.USER, requestDto.getUserPassword());
        }
        catch (DuplicateKeyException e) {
            e.printStackTrace();
            System.out.println("User ID 중복");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public UserType setAdmin(String userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
            UserType newUserType = user.userTypeChange(UserType.ADMIN);
            userRepository.save(user);
            return newUserType;
        }
        catch (NoSuchElementException e) {
            e.printStackTrace();
            System.out.println("존재하지 않는 ID 입니다.");
        }
        return null;
    }

    private void userIdDuplicationCheck(String userId) throws DuplicateKeyException {
        if ( userRepository.findById(userId).isPresent() ) {
            throw new DuplicateKeyException("User ID 중복");
        }
    }

    @Transactional
    private String signup(String userId, String userName, String userEmail, UserType userType, String userPassword) throws NoSuchAlgorithmException {
        User user = User.builder()
                .userId(userId)
                .username(userName)
                .email(userEmail)
                .userType(userType)
                .build();
        Password password = Password.builder()
                .user(user)
                .password(userPassword)
                .build();
        String savedUserId = userRepository.save(user).getUserId();
        passwordRepository.save(password);
        return savedUserId;
    }
}
