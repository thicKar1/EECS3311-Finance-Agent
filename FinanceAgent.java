import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class FinanceAgent {
    private LLMClient llm;
    private Map<String, String> availableTools;

    public FinanceAgent() {
        this.llm = new LLMClient();
        this.availableTools = new LinkedHashMap<>();

        String[] toolNames = {
            "budget", "transaction", "summary", "anomaly", "goal",
            "tax", "debt", "currency", "investment", "bill"
        };

        for (String name : toolNames) {
            FinanceTool tool = ToolFactory.createTool(name);
            if (tool != null) {
                availableTools.put(name, tool.getDescription());
            }
        }
    }

    public String processQuery(String userId, String query) {
        if (query == null || query.trim().isEmpty()) {
            return "Please enter a valid financial question or command.";
        }

        // Step 1: Reason (LLM selects tool and extracts parameters as tool|arg1|arg2)
        System.out.println("\n[Agent] Thinking about which tool to use for: '" + query + "'");
        String decision = llm.analyzeQueryForTool(query, availableTools);

        String contextData;

        // Step 2: Act (Execute the selected tool with extracted arguments)
        if (decision != null && !decision.trim().isEmpty()) {
            String[] parts = decision.split("\\|");
            String selectedToolName = parts[0].trim().toLowerCase();
            String[] toolArgs = (parts.length > 1)
                ? Arrays.copyOfRange(parts, 1, parts.length)
                : new String[]{};

            System.out.println("[Agent] Action Selected: " + selectedToolName + " | Args: " + Arrays.toString(toolArgs));
            try {
                FinanceTool tool = ToolFactory.createTool(selectedToolName);
                contextData = tool.execute(userId, toolArgs);
                System.out.println("[Agent] Data retrieved: " + contextData);
            } catch (Exception e) {
                contextData = "Error executing tool: " + e.getMessage();
            }
        } else {
            System.out.println("[Agent] No specific tool required.");
            contextData = "No external data fetched.";
        }

        // Step 3: Synthesize (Final LLM response strictly grounded in retrieved tool data)
        System.out.println("[Agent] Generating final natural language response...");
        return llm.generateFinalResponse(query, contextData);
    }
}
