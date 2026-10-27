import os
from abc import ABC, abstractmethod

# Strategy Pattern: Define a common interface for all finance tools
class FinanceTool(ABC):
    @abstractmethod
    def execute(self, user_id: str, **kwargs) -> str:
        pass
    
    @abstractmethod
    def get_description(self) -> str:
        pass

# Example Tool 1: Budget Checker
class BudgetTool(FinanceTool):
    def execute(self, user_id: str, **kwargs) -> str:
        # Later, this will connect to your database/CSV
        return f"Fetched budget data for {user_id}: 70% of monthly budget used."

    def get_description(self) -> str:
        return "Fetches the user's current budget and spending status."

# Core Agent that implements meaningful multi-step behavior
class FinanceAgent:
    def __init__(self):
        self.api_key = os.getenv("LLM_API_KEY")
        self.tools = {
            "check_budget": BudgetTool(),
            # You will add more tools here for the 10 features (alerts, summaries, etc.)
        }

    def process_query(self, user_id: str, query: str) -> str:
        """
        ReAct Loop: The agent decides which tool to use, executes it, 
        and formulates a final response based on real data.
        """
        # Step 1: Reason (Simulated LLM decision process)
        print(f"Agent Reasoning: Analyzing query '{query}'")
        selected_tool_name = self._decide_tool(query)
        
        # Step 2: Act (Execute the tool to get context)
        if selected_tool_name and selected_tool_name in self.tools:
            print(f"Agent Action: Executing {selected_tool_name}")
            tool = self.tools[selected_tool_name]
            context = tool.execute(user_id)
        else:
            context = "No specific tool needed."

        # Step 3: Synthesize (Final LLM generation based on tool data)
        # (This is where you will make the actual API call to OpenAI/Gemini/etc.)
        final_answer = f"Based on my analysis: {context} You are on track!"
        return final_answer

    def _decide_tool(self, query: str) -> str:
        # In a full implementation, you send the query and tool descriptions to the LLM 
        # and ask it to return the name of the tool to use.
        if "budget" in query.lower():
            return "check_budget"
        return None

# For testing locally
if __name__ == "__main__":
    agent = FinanceAgent()
    response = agent.process_query("user_123", "How is my budget looking this month?")
    print(f"Final Output: {response}")
