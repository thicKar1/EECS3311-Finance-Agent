import java.util.HashMap;
import java.util.Map;

public class FinanceAgent {
    private LLMClient llm;
    private Map<String, String> availableTools;

    public FinanceAgent() {
        this.llm = new LLMClient();
        this.availableTools = new HashMap<>();
        
        // This map provides descriptions to the LLM so it knows what each tool does
        availableTools.put("budget", "Fetches the user's current budget and spending status.");
        availableTools.put("transaction", "Allows the user to enter a new financial transaction.");
    }

    public String processQuery(String userId, String query) {
        // Step 1: Reason (LLM decision process)
        System.out.println("\n[Agent] Thinking about which tool to use for: '" + query + "'");
        String selectedToolName = llm.analyzeQueryForTool(query, availableTools);
        
        String contextData;
        
        // Step 2: Act (Execute the tool using the Factory)
        if (selectedToolName != null) {
            System.out.println("[Agent] Action Selected: " + selectedToolName);
            try {
                FinanceTool tool = ToolFactory.createTool(selectedToolName);
                contextData = tool.execute(userId, new String[]{}); // Args would be parsed by LLM in full version
                System.out.println("[Agent] Data retrieved: " + contextData);
            } catch (Exception e) {
                contextData = "Error executing tool: " + e.getMessage();
            }
        } else {
            System.out.println("[Agent] No specific tool required.");
            contextData = "No external data fetched.";
        }

        // Step 3: Synthesize (Final LLM generation based on tool data)
        System.out.println("[Agent] Generating final natural language response...");
        return llm.generateFinalResponse(query, contextData);
    }
}
