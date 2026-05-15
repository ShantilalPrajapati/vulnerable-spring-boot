package com.example.vulnerable;

import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class VulnerableController {

    // Vulnerability 1: SQL Injection
    @GetMapping("/user")
    public Map<String, Object> getUser(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
            Statement stmt = conn.createStatement();
            // SQL Injection vulnerability - directly concatenating user input
            String query = "SELECT * FROM users WHERE username = '" + username + "'";
            ResultSet rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                result.put("username", rs.getString("username"));
                result.put("email", rs.getString("email"));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }
        return result;
    }

    // Vulnerability 2: Command Injection
    @GetMapping("/ping")
    public Map<String, String> ping(@RequestParam String host) {
        Map<String, String> result = new HashMap<>();
        try {
            // Command Injection vulnerability - directly using user input in system command
            Process process = Runtime.getRuntime().exec("ping -n 1 " + host);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            result.put("output", output.toString());
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }
        return result;
    }

    // Vulnerability 3: Path Traversal
    @GetMapping("/file")
    public Map<String, String> readFile(@RequestParam String filename) {
        Map<String, String> result = new HashMap<>();
        try {
            // Path Traversal vulnerability - no validation of file path
            java.io.File file = new java.io.File(filename);
            BufferedReader reader = new BufferedReader(new java.io.FileReader(file));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            result.put("content", content.toString());
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }
        return result;
    }

    // Vulnerability 4: Hardcoded Credentials
    @PostMapping("/admin/login")
    public Map<String, Object> adminLogin(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        // Hardcoded credentials vulnerability
        String adminUsername = "admin";
        String adminPassword = "admin123";
        
        if (username.equals(adminUsername) && password.equals(adminPassword)) {
            result.put("success", true);
            result.put("message", "Admin login successful");
            result.put("token", "hardcoded-jwt-token-12345");
        } else {
            result.put("success", false);
            result.put("message", "Invalid credentials");
        }
        return result;
    }

    // Vulnerability 5: Sensitive Data Exposure
    @GetMapping("/config")
    public Map<String, String> getConfig() {
        Map<String, String> config = new HashMap<>();
        // Exposing sensitive configuration data
        config.put("database_url", "jdbc:mysql://localhost:3306/mydb");
        config.put("database_username", "root");
        config.put("database_password", "root123");
        config.put("api_key", "sk-1234567890abcdef");
        config.put("secret_key", "my-super-secret-key");
        return config;
    }

    // Vulnerability 6: XML External Entity (XXE)
    @PostMapping("/parse-xml")
    public Map<String, String> parseXml(@RequestBody String xmlContent) {
        Map<String, String> result = new HashMap<>();
        try {
            // XXE vulnerability - no protection against external entities
            javax.xml.parsers.DocumentBuilderFactory factory = 
                javax.xml.parsers.DocumentBuilderFactory.newInstance();
            // Not disabling external entities - vulnerable to XXE
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(
                new java.io.ByteArrayInputStream(xmlContent.getBytes())
            );
            result.put("status", "parsed");
            result.put("root", doc.getDocumentElement().getNodeName());
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }
        return result;
    }

    // Vulnerability 7: Insecure Deserialization
    @PostMapping("/deserialize")
    public Map<String, String> deserialize(@RequestBody byte[] data) {
        Map<String, String> result = new HashMap<>();
        try {
            // Insecure deserialization vulnerability
            java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(data);
            java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bis);
            Object obj = ois.readObject();
            result.put("status", "deserialized");
            result.put("type", obj.getClass().getName());
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }
        return result;
    }

    // Vulnerability 8: Missing Authentication
    @DeleteMapping("/user/{id}")
    public Map<String, String> deleteUser(@PathVariable String id) {
        Map<String, String> result = new HashMap<>();
        // No authentication check - anyone can delete users
        result.put("message", "User " + id + " deleted successfully");
        result.put("status", "success");
        return result;
    }

    // Vulnerability 9: Server-Side Request Forgery (SSRF)
    @GetMapping("/fetch")
    public Map<String, String> fetchUrl(@RequestParam String url) {
        Map<String, String> result = new HashMap<>();
        try {
            // SSRF vulnerability - no validation of URL
            java.net.URL targetUrl = new java.net.URL(url);
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(targetUrl.openStream())
            );
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            result.put("content", content.toString());
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }
        return result;
    }

    // Vulnerability 10: Weak Cryptography
    @PostMapping("/encrypt")
    public Map<String, String> encrypt(@RequestParam String data) {
        Map<String, String> result = new HashMap<>();
        try {
            // Using weak/deprecated encryption algorithm
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("DES");
            javax.crypto.KeyGenerator keyGen = javax.crypto.KeyGenerator.getInstance("DES");
            javax.crypto.SecretKey key = keyGen.generateKey();
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            result.put("encrypted", java.util.Base64.getEncoder().encodeToString(encrypted));
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }
        return result;
    }
}

// Made with Bob
