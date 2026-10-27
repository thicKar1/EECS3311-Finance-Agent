import os
from abc import ABC, abstractmethod
from llm_client import LLMClient
from tool_factory import ToolFactory

# Strategy Pattern: Define a common interface for all finance tools
class FinanceTool(ABC):
    @abstractmethod
    def execute(self, user_id: str, **kwargs) -> str:
        pass
    
    @abstractmethod
    def get_description(self) -> str:
        pass

# Core Agent that implements meaningful multi-step behavior
class FinanceAgent:
    def __init__(self):
        self.llm = LLMClient()
        self.factory = ToolFactory()
        
        # This maps tool keys to their descriptions so the LLM knows what they do
        self.available_tools = {
            "budget": "Fetches the user's current budget and spending status.",
            "transaction": "Allows the user to enter a new financial transaction.",
            "summary": "Provides a summary of recent transactions and spending.",
            "anomaly": "Detects anomalies or unusually high transactions."
        }

    def process_query(self, user_id: str, query: str) -> str:
        """
        True ReAct Loop: LLM decides the tool -> Factory builds the tool -> 
        Tool fetches data -> LLM generates final answer.
        """
        # Step 1: Reason (LLM decision process)
        print(f"\n[Agent] Thinking about which tool to use for: '{query}'")
        selected_tool_name = self.llm.analyze_query_for_tool(query, self.available_tools)
        
        # Step 2: Act (Execute the tool using the Factory)
        if selected_tool_name and selected_tool_name != "NONE":
            print(f"[Agent] Action Selected: {selected_tool_name}")
            try:
                tool = self.factory.create_tool(selected_tool_name)
                context_data = tool.execute(user_id)
                print(f"[Agent] Data retrieved: {context_data}")
            except Exception as e:
                context_data = f"Error executing tool: {e}"
        else:
            print("[Agent] No specific tool required.")
            context_data = "No external data fetched."

        # Step 3: Synthesize (Final LLM generation based on tool data)
        print("[Agent] Generating final natural language response...")
        final_answer = self.llm.generate_final_response(query, context_data)
        return final_answer
