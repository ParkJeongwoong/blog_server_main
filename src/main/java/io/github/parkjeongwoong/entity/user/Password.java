package io.github.parkjeongwoong.entity.user;

import io.github.parkjeongwoong.entity.BaseTimeEntity;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@NoArgsConstructor
@Entity
public class Password extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String salt;

    @Column(nullable = false)
    private int stretching;

    @Column(nullable = false)
    private String password;

    @Column
    private boolean renewed;

    @Builder
    public Password(User user, String password) throws NoSuchAlgorithmException {
        this.renewed = false;
        this.stretching = 0;
        this.user = user;
        this.password = passwordEncryption(password);
    }

    public boolean checkPassword(String inputPassword) {
        try {
            return this.password.equals(passwordEncryption(inputPassword));
        } catch (NoSuchAlgorithmException e) {
            log.error("Password Check Error", e);
            return false;
        }
    }

    public boolean checkRenewed() {
        return this.renewed;
    }

    public boolean changePassword(String oldPassword, String newPassword) throws NoSuchAlgorithmException {
        if (checkPassword(oldPassword)) {
            this.password = passwordEncryption(newPassword);
            this.renewed = false;
            return true;
        }
        return false;
    }

    public String renewPassword() throws NoSuchAlgorithmException {
        this.renewed = true;
        this.salt = getSalt();
        this.stretching = getStretching();
        this.password = passwordEncryption(this.password);
        return this.password;
    }

    private String passwordEncryption(String inputPassword) throws NoSuchAlgorithmException {
        if (this.salt == null) { this.salt = getSalt(); }
        if (this.stretching == 0) { this.stretching = getStretching(); }
        return getEncrypt(inputPassword, this.salt, this.stretching);
    }

    private String getSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] rootSalt = new byte[20];
        secureRandom.nextBytes(rootSalt); // 난수 생성
        return new String(Base64.getEncoder().encode(rootSalt));
    }

    private int getStretching() {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextInt(5)+1;
    }

    private String getEncrypt(String password, String salt, int streching) throws NoSuchAlgorithmException {
        // Salting
        String saltedPassword = password+salt;
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] bytesPassword = saltedPassword.getBytes();
        // Stretching
        for (int i=0;i<streching;i++) {
            messageDigest.update(bytesPassword);
            bytesPassword = messageDigest.digest();
        }
        return bytesToHexadecimal(bytesPassword);
    }

    private String bytesToHexadecimal(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) { stringBuilder.append(String.format("%02x", b)); }
        return stringBuilder.toString();
    }

}
