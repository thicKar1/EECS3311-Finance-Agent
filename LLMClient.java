import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

public class LLMClient {
    private String apiKey;
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    public LLMClient() {
        this.apiKey = loadApiKey();
    }

    private String loadApiKey() {
        String key = System.getenv("LLM_API_KEY");
        if (key != null && !key.trim().isEmpty()) {
            return key.trim();
        }
        try (FileInputStream fis = new FileInputStream(".env")) {
            Properties props = new Properties();
            props.load(fis);
            key = props.getProperty("LLM_API_KEY");
            if (key != null) return key.trim();
        } catch (Exception ignored) {}
        return "";
    }

    public String analyzeQueryForTool(String query, Map<String, String> availableTools) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.contains("YOUR_LOCAL_API_KEY")) {
            return fallbackTool(query);
        }

        String prompt = "You are a financial agent router. Select the best tool from: " + availableTools.keySet().toString()
                + " for query: '" + query + "'. Reply ONLY with the exact tool name string.";

        String response = callLLM(prompt);
        if (response != null) {
            String clean = response.trim().toLowerCase();
            for (String tool : availableTools.keySet()) {
                if (clean.contains(tool.toLowerCase())) {
                    return tool;
                }
            }
        }
        return fallbackTool(query);
    }

    public String generateFinalResponse(String query, String contextData) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.contains("YOUR_LOCAL_API_KEY")) {
            return fallbackSynthesis(query, contextData);
        }

        String prompt = "User Query: " + query + "\nRetrieved Financial Data: " + contextData 
                + "\nSynthesize a clear, professional, natural language response based on this data.";

        String response = callLLM(prompt);
        if (response != null && !response.trim().isEmpty()) {
            return response;
        }
        return fallbackSynthesis(query, contextData);
    }

    private String callLLM(String prompt) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("HTTP-Referer", "https://github.com/thickKar1/EECS3311-Finance-Agent");
            conn.setRequestProperty("X-Title", "Personal Finance AI Agent");
            conn.setDoOutput(true);

            String escapedPrompt = prompt.replace("\\", "\\\\")
                                         .replace("\"", "\\\"")
                                         .replace("\n", " ")
                                         .replace("\r", " ");
                                         
            String jsonInputString = "{"
                    + "\"model\": \"openrouter/free\","
                    + "\"messages\": [{\"role\": \"user\", \"content\": \"" + escapedPrompt + "\"}]"
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return parseContentFromJson(response.toString());
                }
            } else {
                System.out.println("[LLM] OpenRouter HTTP " + code + " - using fallback.");
                return null;
            }
        } catch (Exception e) {
            System.out.println("[LLM] Connection note: " + e.getMessage());
            return null;
        }
    }

    private String parseContentFromJson(String json) {
        try {
            int contentIdx = json.indexOf("\"content\":");
            if (contentIdx != -1) {
                int start = json.indexOf("\"", contentIdx + 10) + 1;
                int end = json.indexOf("\"", start);
                if (start > 0 && end > start) {
                    String extracted = json.substring(start, end);
                    return extracted.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
                }
            }
        } catch (Exception ignored) {}
        return json;
    }

    private String fallbackTool(String prompt) {
        String lower = prompt.toLowerCase();
        if (lower.contains("budget")) return "budget";
        if (lower.contains("transaction")) return "transaction";
        if (lower.contains("bill")) return "bill";
        if (lower.contains("anomaly")) return "anomaly";
        if (lower.contains("currency")) return "currency";
        if (lower.contains("debt")) return "debt";
        if (lower.contains("goal")) return "goal";
        if (lower.contains("investment")) return "investment";
        if (lower.contains("tax")) return "tax";
        return "summary";
    }

    private String fallbackSynthesis(String query, String contextData) {
        return "[Simulated Output] Based on the data (" + contextData + "), your request has been processed.";
    }
}