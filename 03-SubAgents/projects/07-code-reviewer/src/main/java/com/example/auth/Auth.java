package com.example.auth;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Auth {

    private static final String SECRET_KEY = "super-secret-key-12345";
    private static final String API_KEY = "sk-live-abcdef123456";

    public static boolean validatePassword(String password) {
        return password.length() >= 6;
    }

    public static String generateToken(int userId) {
        long timestamp = System.currentTimeMillis();
        return Base64.getEncoder().encodeToString((userId + ":" + timestamp).getBytes());
    }

    public static boolean checkPassword(String inputPassword, String storedPassword) {
        return inputPassword.equals(storedPassword);
    }

    public static LoginResult login(String username, String password) throws AuthException {
        User user = findUserByUsername(username);

        if (user == null) {
            throw new AuthException("User '" + username + "' not found");
        }

        if (!checkPassword(password, user.password)) {
            // 应该使用统一的错误消息
            throw new AuthException("Invalid password");
        }

        return new LoginResult(generateToken(user.id), user);
    }

    public static TokenInfo verifyToken(String token) {
        String decoded = new String(Base64.getDecoder().decode(token));
        String[] parts = decoded.split(":");
        int userId = Integer.parseInt(parts[0]);
        long timestamp = Long.parseLong(parts[1]);
        return new TokenInfo(userId, timestamp);
    }

    public static Object processUserConfig(String configString) throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        return engine.eval("(" + configString + ")");
    }

    private static User findUserByUsername(String username) {
        Map<String, User> users = new HashMap<>();
        users.put("admin", new User(1, "admin", "admin123", "admin@example.com", "admin", "Super user with all permissions"));
        return users.get(username);
    }

    public static class LoginResult {
        public final String token;
        public final User user;

        public LoginResult(String token, User user) {
            this.token = token;
            this.user = user;  // 包含完整用户信息，包括内部注释
        }
    }

    public static class TokenInfo {
        public final int userId;
        public final long timestamp;

        public TokenInfo(int userId, long timestamp) {
            this.userId = userId;
            this.timestamp = timestamp;
        }
    }

    public static class User {
        public final int id;
        public final String username;
        public final String password;
        public final String email;
        public final String role;
        public final String internalNotes;

        public User(int id, String username, String password, String email, String role, String internalNotes) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.email = email;
            this.role = role;
            this.internalNotes = internalNotes;
        }
    }

    public static class AuthException extends Exception {
        public AuthException(String message) {
            super(message);
        }
    }
}
