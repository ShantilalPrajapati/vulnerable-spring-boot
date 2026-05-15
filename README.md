# Vulnerable Spring Boot Application

⚠️ **WARNING: This application contains intentional security vulnerabilities for educational and testing purposes only. DO NOT deploy this application in production or expose it to the internet!**

## Overview

This is a deliberately vulnerable Spring Boot application designed for:
- Security testing and penetration testing practice
- Learning about common web application vulnerabilities
- Testing security scanning tools
- Security awareness training

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## How to Run

```bash
cd vulnerable-spring-boot
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Vulnerabilities Included

### 1. SQL Injection (CWE-89)
**Endpoint:** `GET /api/user?username=<value>`

**Description:** User input is directly concatenated into SQL queries without parameterization.

**Example Attack:**
```bash
curl "http://localhost:8080/api/user?username=admin' OR '1'='1"
```

### 2. Command Injection (CWE-78)
**Endpoint:** `GET /api/ping?host=<value>`

**Description:** User input is directly passed to system commands without validation.

**Example Attack:**
```bash
curl "http://localhost:8080/api/ping?host=127.0.0.1%26%26dir"
```

### 3. Path Traversal (CWE-22)
**Endpoint:** `GET /api/file?filename=<value>`

**Description:** No validation of file paths allows reading arbitrary files.

**Example Attack:**
```bash
curl "http://localhost:8080/api/file?filename=../../../etc/passwd"
curl "http://localhost:8080/api/file?filename=C:/Windows/System32/drivers/etc/hosts"
```

### 4. Hardcoded Credentials (CWE-798)
**Endpoint:** `POST /api/admin/login`

**Description:** Admin credentials are hardcoded in the source code.

**Credentials:**
- Username: `admin`
- Password: `admin123`

**Example:**
```bash
curl -X POST "http://localhost:8080/api/admin/login?username=admin&password=admin123"
```

### 5. Sensitive Data Exposure (CWE-200)
**Endpoint:** `GET /api/config`

**Description:** Sensitive configuration data including passwords and API keys are exposed.

**Example:**
```bash
curl "http://localhost:8080/api/config"
```

### 6. XML External Entity (XXE) (CWE-611)
**Endpoint:** `POST /api/parse-xml`

**Description:** XML parser doesn't disable external entities, allowing XXE attacks.

**Example Attack:**
```bash
curl -X POST "http://localhost:8080/api/parse-xml" \
  -H "Content-Type: application/xml" \
  -d '<?xml version="1.0"?><!DOCTYPE foo [<!ENTITY xxe SYSTEM "file:///etc/passwd">]><root>&xxe;</root>'
```

### 7. Insecure Deserialization (CWE-502)
**Endpoint:** `POST /api/deserialize`

**Description:** Accepts and deserializes untrusted data without validation.

**Risk:** Can lead to remote code execution.

### 8. Missing Authentication (CWE-306)
**Endpoint:** `DELETE /api/user/{id}`

**Description:** Critical operations can be performed without authentication.

**Example:**
```bash
curl -X DELETE "http://localhost:8080/api/user/123"
```

### 9. Server-Side Request Forgery (SSRF) (CWE-918)
**Endpoint:** `GET /api/fetch?url=<value>`

**Description:** Server fetches arbitrary URLs without validation.

**Example Attack:**
```bash
curl "http://localhost:8080/api/fetch?url=http://localhost:8080/api/config"
curl "http://localhost:8080/api/fetch?url=http://169.254.169.254/latest/meta-data/"
```

### 10. Weak Cryptography (CWE-327)
**Endpoint:** `POST /api/encrypt?data=<value>`

**Description:** Uses deprecated DES encryption algorithm.

**Example:**
```bash
curl -X POST "http://localhost:8080/api/encrypt?data=sensitive-data"
```

### 11. Vulnerable Dependencies (CVE-2021-44228)
**Component:** Log4j 2.14.1

**Description:** Uses vulnerable version of Log4j susceptible to Log4Shell vulnerability.

**Risk:** Remote code execution via JNDI injection.

### 12. Exposed Actuator Endpoints (CWE-200)
**Endpoints:** `/actuator/*`

**Description:** All Spring Boot Actuator endpoints are exposed without authentication.

**Examples:**
```bash
curl "http://localhost:8080/actuator/health"
curl "http://localhost:8080/actuator/env"
curl "http://localhost:8080/actuator/metrics"
curl "http://localhost:8080/actuator/mappings"
```

### 13. Exposed H2 Console (CWE-200)
**Endpoint:** `/h2-console`

**Description:** H2 database console is accessible without authentication.

**Access:** Navigate to `http://localhost:8080/h2-console`

### 14. Insecure CORS Configuration (CWE-942)
**Description:** CORS allows all origins, methods, and headers.

**Risk:** Cross-origin attacks possible from any domain.

### 15. Verbose Error Messages (CWE-209)
**Description:** Detailed error messages including stack traces are exposed.

**Risk:** Information disclosure about application internals.

### 16. Insecure Session Configuration (CWE-614)
**Description:** 
- Session cookies are not marked as secure
- Session cookies are not HTTP-only
- Long session timeout (30 days)

### 17. Debug Logging Enabled (CWE-532)
**Description:** Debug-level logging is enabled, potentially logging sensitive data.

## OWASP Top 10 Coverage

This application demonstrates vulnerabilities from the OWASP Top 10:

1. **A01:2021 – Broken Access Control** - Missing authentication, exposed endpoints
2. **A02:2021 – Cryptographic Failures** - Weak encryption, exposed sensitive data
3. **A03:2021 – Injection** - SQL injection, command injection, XXE
4. **A04:2021 – Insecure Design** - Multiple design flaws
5. **A05:2021 – Security Misconfiguration** - Exposed actuator, H2 console, debug mode
6. **A06:2021 – Vulnerable and Outdated Components** - Log4j vulnerability
7. **A08:2021 – Software and Data Integrity Failures** - Insecure deserialization
8. **A09:2021 – Security Logging and Monitoring Failures** - Verbose logging
9. **A10:2021 – Server-Side Request Forgery** - SSRF vulnerability

## Security Testing Tools

You can test this application with:
- **OWASP ZAP** - Web application security scanner
- **Burp Suite** - Web vulnerability scanner
- **SQLMap** - SQL injection testing
- **Nikto** - Web server scanner
- **Dependency-Check** - Vulnerable dependency scanner
- **SonarQube** - Code quality and security analysis

## Remediation Guide

For each vulnerability, here are the recommended fixes:

1. **SQL Injection**: Use PreparedStatement or JPA with parameterized queries
2. **Command Injection**: Avoid Runtime.exec(), validate and sanitize input
3. **Path Traversal**: Validate file paths, use whitelisting
4. **Hardcoded Credentials**: Use environment variables or secure vaults
5. **Sensitive Data Exposure**: Never expose credentials in APIs
6. **XXE**: Disable external entities in XML parsers
7. **Insecure Deserialization**: Avoid deserializing untrusted data
8. **Missing Authentication**: Implement Spring Security
9. **SSRF**: Validate and whitelist URLs
10. **Weak Cryptography**: Use AES-256 or stronger algorithms
11. **Vulnerable Dependencies**: Update to latest secure versions
12. **Exposed Endpoints**: Secure actuator endpoints with authentication
13. **CORS**: Configure specific allowed origins
14. **Error Messages**: Use generic error messages in production
15. **Session Security**: Enable secure and HTTP-only flags

## Disclaimer

This application is for educational purposes only. The authors are not responsible for any misuse of this code. Always obtain proper authorization before testing security vulnerabilities on any system.

## License

MIT License - Use at your own risk for educational purposes only.