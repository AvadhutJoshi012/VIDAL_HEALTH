package com.example.qualifier;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class StartupRunner {

    @Bean
    public CommandLineRunner run() {
        return args -> {
            RestTemplate restTemplate = new RestTemplate();

            String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            Map<String, String> body = new HashMap<>();
            body.put("name", "AvadhutRavindraJohi");
            body.put("regNo", "250850120042");
            body.put("email", "avadhutjoshi012@gmail.com");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(generateUrl, request, Map.class);

            String webhookUrl = response.getBody().get("webhook").toString();
            String accessToken = response.getBody().get("accessToken").toString();

            String finalQuery =
                "SELECT d.DEPARTMENT_NAME, " +
                "ROUND(AVG(TIMESTAMPDIFF(YEAR, e.DOB, CURDATE())), 2) AS AVERAGE_AGE, " +
                "GROUP_CONCAT(CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) ORDER BY e.EMP_ID SEPARATOR ', ') AS EMPLOYEE_LIST " +
                "FROM DEPARTMENT d " +
                "JOIN EMPLOYEE e ON d.DEPARTMENT_ID = e.DEPARTMENT " +
                "JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID " +
                "WHERE p.AMOUNT > 70000 " +
                "GROUP BY d.DEPARTMENT_ID, d.DEPARTMENT_NAME " +
                "ORDER BY d.DEPARTMENT_ID DESC";

            HttpHeaders webhookHeaders = new HttpHeaders();
            webhookHeaders.setContentType(MediaType.APPLICATION_JSON);
            webhookHeaders.set("Authorization", accessToken);

            Map<String, String> webhookBody = new HashMap<>();
            webhookBody.put("finalQuery", finalQuery);

            HttpEntity<Map<String, String>> webhookRequest =
                    new HttpEntity<>(webhookBody, webhookHeaders);

            ResponseEntity<String> webhookResponse =
                    restTemplate.postForEntity(webhookUrl, webhookRequest, String.class);

            System.out.println("Webhook Response: " + webhookResponse.getBody());
        };
    }
}
