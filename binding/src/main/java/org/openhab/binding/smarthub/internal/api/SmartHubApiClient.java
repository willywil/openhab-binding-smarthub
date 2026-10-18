package org.openhab.binding.smarthub.internal.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Simple HTTP client for SmartHub API.
 * Based on the authentication and polling flow from the Home Assistant integration.
 */
@NonNullByDefault
public class SmartHubApiClient {

    private final Logger logger = LoggerFactory.getLogger(SmartHubApiClient.class);
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    private final Gson gson = new Gson();

    private String host;
    private String email;
    private String password;
    private String token;

    public SmartHubApiClient(String host, String email, String password) {
        this.host = host.replace("https://", "").replace("http://", "");
        this.email = email;
        this.password = password;
    }

    public boolean authenticate() {
        try {
            String url = "https://" + host + "/services/oauth/auth/v2";
            String body = "userId=" + java.net.URLEncoder.encode(email, "UTF-8")
                    + "&password=" + java.net.URLEncoder.encode(password, "UTF-8");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("User-Agent", "openHAB SmartHub Binding")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject json = gson.fromJson(response.body(), JsonObject.class);
                if (json.has("authorizationToken")) {
                    this.token = json.get("authorizationToken").getAsString();
                    logger.info("Successfully authenticated to SmartHub");
                    return true;
                }
            }
            logger.warn("Authentication failed: HTTP {}", response.statusCode());
            return false;
        } catch (Exception e) {
            logger.error("Authentication error", e);
            return false;
        }
    }

    public String getUsageData(String accountId, String locationId, String timeFrame) {
        if (token == null && !authenticate()) {
            return null;
        }

        try {
            String url = "https://" + host + "/services/secured/utility-usage/poll";

            JsonObject payload = new JsonObject();
            payload.addProperty("timeFrame", timeFrame);
            payload.addProperty("userId", email);
            payload.addProperty("screen", "USAGE_EXPLORER");
            payload.addProperty("includeDemand", false);
            payload.addProperty("serviceLocationNumber", locationId);
            payload.addProperty("accountNumber", accountId);
            payload.add("industries", gson.toJsonTree(new String[]{"ELECTRIC"}));
            // Add start/end timestamps as needed (simplified here)

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .header("X-Nisc-Smarthub-Username", email)
                    .header("User-Agent", "openHAB SmartHub Binding")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(payload)))
                    .timeout(Duration.ofSeconds(60))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else if (response.statusCode() == 401) {
                token = null;
                return getUsageData(accountId, locationId, timeFrame); // retry once
            }
            logger.warn("Usage poll failed: HTTP {}", response.statusCode());
            return null;
        } catch (Exception e) {
            logger.error("Error fetching usage data", e);
            return null;
        }
    }
}
