import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            return fallbackToolWithArgs(query);
        }

        StringBuilder toolList = new StringBuilder();
        for (Map.Entry<String, String> entry : availableTools.entrySet()) {
            toolList.append("- ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }

        String prompt = "You are a financial agent router. Select the single best tool and extract arguments from the user query.\n"
                + "Available tools:\n" + toolList
                + "\nReply ONLY in the exact pipe-delimited format: toolName|arg1|arg2 (or just toolName if no arguments are needed). No extra words.\n"
                + "Examples:\n"
                + "- 'Log $45.50 for Groceries' -> transaction|45.50|Groceries\n"
                + "- 'What is my budget?' -> budget\n"
                + "- 'Set my budget limit to 2500' -> budget|2500\n"
                + "- 'Show my spending summary' -> summary\n"
                + "- 'Check for unusual spending' -> anomaly\n"
                + "- 'Convert 200 USD to CAD' -> currency|200|USD|CAD\n"
                + "- 'Estimate tax on 75000 income' -> tax|75000\n"
                + "- 'Pay 250 per month toward debt' -> debt|250\n"
                + "- 'Deposit 400 into my savings goal' -> goal|400\n"
                + "- 'Invest 1000 into my portfolio' -> investment|1000\n"
                + "- 'Add Internet bill of 90 due in 5 days' -> bill|Internet|90|5\n"
                + "\nUser Query: " + query;

        String response = callLLM(prompt);
        if (response != null) {
            String firstLine = response.split("\\r?\\n")[0].replace("`", "").trim();
            String[] parts = firstLine.split("\\|");
            String candidateTool = parts[0].trim().toLowerCase();

            for (String tool : availableTools.keySet()) {
                if (candidateTool.equals(tool.toLowerCase()) || candidateTool.contains(tool.toLowerCase())) {
                    if (parts.length > 1) {
                        StringBuilder rebuilt = new StringBuilder(tool);
                        for (int i = 1; i < parts.length; i++) {
                            rebuilt.append("|").append(parts[i].trim());
                        }
                        return rebuilt.toString();
                    }
                    return tool;
                }
            }
        }
        return fallbackToolWithArgs(query);
    }

    public String generateFinalResponse(String query, String contextData) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.contains("YOUR_LOCAL_API_KEY")) {
            return fallbackSynthesis(contextData);
        }

        String prompt = "User Query: " + query + "\n"
                + "Retrieved Financial Data: " + contextData + "\n"
                + "Rules: Base your answer strictly on the Retrieved Financial Data above. "
                + "Do not invent balances or transactions. If the data starts with 'Error:', clearly explain the error to the user.";

        String response = callLLM(prompt);
        if (response != null && !response.trim().isEmpty()) {
            return response.trim();
        }
        return fallbackSynthesis(contextData);
    }

    private String callLLM(String prompt) {
        try {
            URL url = URI.create(API_URL).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("HTTP-Referer", "https://github.com/thickKar1/EECS3311-Finance-Agent");
            conn.setRequestProperty("X-Title", "Personal Finance AI Agent");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);
            conn.setDoOutput(true);

            String escapedPrompt = prompt.replace("\\", "\\\\")
                                         .replace("\"", "\\\"")
                                         .replace("\n", "\\n")
                                         .replace("\r", "");

            String jsonInputString = "{"
                    + "\"model\": \"openrouter/free\","
                    + "\"messages\": [{\"role\": \"user\", \"content\": \"" + escapedPrompt + "\"}]"
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return parseContentFromJson(response.toString());
                }
            } else {
                System.out.println("[LLM] OpenRouter HTTP " + code + " - using local fallback.");
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
            if (contentIdx == -1) return null;

            int start = json.indexOf("\"", contentIdx + 10);
            if (start == -1) return null;
            start++;

            StringBuilder sb = new StringBuilder();
            boolean escaping = false;
            for (int i = start; i < json.length(); i++) {
                char c = json.charAt(i);
                if (escaping) {
                    if (c == 'n') sb.append('\n');
                    else if (c == 'r') sb.append('\r');
                    else if (c == 't') sb.append('\t');
                    else sb.append(c);
                    escaping = false;
                } else if (c == '\\') {
                    escaping = true;
                } else if (c == '"') {
                    return sb.toString();
                } else {
                    sb.append(c);
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String fallbackToolWithArgs(String prompt) {
        String lower = prompt.toLowerCase();
        String firstNum = extractFirstNumber(prompt);

        if (lower.contains("log") || lower.contains("spend") || lower.contains("spent")
                || lower.contains("transaction") || lower.contains("expense") || lower.contains("bought")) {
            String category = extractCategory(prompt);
            if (!firstNum.isEmpty()) {
                return "transaction|" + firstNum + "|" + category;
            }
            return "transaction";
        }
        if (lower.contains("budget") || lower.contains("limit")) {
            return firstNum.isEmpty() ? "budget" : "budget|" + firstNum;
        }
        if (lower.contains("bill") || lower.contains("due")) {
            return "bill";
        }
        if (lower.contains("anomal") || lower.contains("unusual") || lower.contains("spike")) {
            return "anomaly";
        }
        if (lower.contains("currency") || lower.contains("convert") || lower.contains("usd") || lower.contains("cad")) {
            String amt = firstNum.isEmpty() ? "100" : firstNum;
            int usdIdx = lower.indexOf("usd");
            int cadIdx = lower.indexOf("cad");
            if (cadIdx != -1 && (usdIdx == -1 || cadIdx < usdIdx)) {
                return "currency|" + amt + "|CAD|USD";
            }
            return "currency|" + amt + "|USD|CAD";
        }
        if (lower.contains("debt") || lower.contains("loan") || lower.contains("payoff")) {
            return firstNum.isEmpty() ? "debt" : "debt|" + firstNum;
        }
        if (lower.contains("goal") || lower.contains("saving") || lower.contains("vacation")) {
            return firstNum.isEmpty() ? "goal" : "goal|" + firstNum;
        }
        if (lower.contains("invest") || lower.contains("portfolio") || lower.contains("stock")) {
            return firstNum.isEmpty() ? "investment" : "investment|" + firstNum;
        }
        if (lower.contains("tax") || lower.contains("income") || lower.contains("bracket")) {
            return firstNum.isEmpty() ? "tax" : "tax|" + firstNum;
        }
        return "summary";
    }

    private String extractFirstNumber(String text) {
        Matcher m = Pattern.compile("-?\\d+(?:\\.\\d+)?").matcher(text.replace(",", ""));
        return m.find() ? m.group() : "";
    }

    private String extractCategory(String text) {
        Matcher m = Pattern.compile("(?i)(?:for|on|under|category)\\s+([A-Za-z]+)").matcher(text);
        if (m.find()) {
            String cat = m.group(1);
            return cat.substring(0, 1).toUpperCase() + cat.substring(1).toLowerCase();
        }
        return "General";
    }

    private String fallbackSynthesis(String contextData) {
        return contextData;
    }
}
