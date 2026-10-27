from agent_core import FinanceTool, BudgetTool

# We will define a few more tool stubs here to build up to your 10 required features

class TransactionTool(FinanceTool):
    def execute(self, user_id: str, **kwargs) -> str:
        return "Transaction entered successfully."
        
    def get_description(self) -> str:
        return "Allows the user to enter a new financial transaction."

class SummaryTool(FinanceTool):
    def execute(self, user_id: str, **kwargs) -> str:
        return "Here is the summary of your spending for this month."
        
    def get_description(self) -> str:
        return "Provides a summary of recent transactions and spending."

class AnomalyTool(FinanceTool):
    def execute(self, user_id: str, **kwargs) -> str:
        return "No unusual spending detected this week."
        
    def get_description(self) -> str:
        return "Detects anomalies or unusually high transactions in the user's account."

# Factory Pattern: Centralized creation of tool objects
class ToolFactory:
    @staticmethod
    def create_tool(tool_type: str) -> FinanceTool:
        """
        Creates and returns the appropriate FinanceTool instance based on the requested type.
        """
        tools = {
            "budget": BudgetTool,
            "transaction": TransactionTool,
            "summary": SummaryTool,
            "anomaly": AnomalyTool
        }
        
        tool_class = tools.get(tool_type)
        if tool_class:
            return tool_class()
        else:
            raise ValueError(f"Unknown tool type requested: {tool_type}")
