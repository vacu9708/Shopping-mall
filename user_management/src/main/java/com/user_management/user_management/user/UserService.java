package com.user_management.user_management.user;

import java.util.Map;

public class UserService {

    public boolean registerUser(UserCredentialsDto userCredentialsDto) {
        return false;
    }

    public Map<String, String> login(UserCredentialsDto userCredentialsDto) {
        return null;
    }

    public String verifyToken(UserCredentialsDto accessToken) {
        return null;
    }

    public Map<String, String> reissueToken(String refreshToken) {
        return null;
    }

    public boolean deleteUser(String accessToken) {
        return false;
    }
    
}
