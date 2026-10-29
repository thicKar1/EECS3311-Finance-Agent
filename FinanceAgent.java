import java.util.HashMap;
import java.util.Map;

public class FinanceAgent {
    private String apiKey;
    private Map<String, FinanceTool> tools;

    public FinanceAgent() {
        this.apiKey = System.getenv("LLM_API_KEY");
        this.tools = new HashMap<>();
        this.tools.put("check_budget", new BudgetTool());
    }

    public String processQuery(String userId, String query) {
        // Step 1: Reason
        System.out.println("Agent Reasoning: Analyzing query '" + query + "'");
        String selectedTool = decideTool(query);
        
        // Step 2: Act
        String context = "No specific tool needed.";
        if (selectedTool != null && tools.containsKey(selectedTool)) {
            System.out.println("Agent Action: Executing " + selectedTool);
            context = tools.get(selectedTool).execute(userId, new String[]{});
        }

        // Step 3: Synthesize
        return "Based on my analysis: " + context + " You are on track!";
    }

    private String decideTool(String query) {
        if (query.toLowerCase().contains("budget")) {
            return "check_budget";
        }
        return null;
    }

    public static void main(String[] args) {
        FinanceAgent agent = new FinanceAgent();
        System.out.println("Final Output: " + agent.processQuery("user_123", "How is my budget looking?"));
    }
}
