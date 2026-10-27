import os
import json

# Ensure you run: pip install openai
from openai import OpenAI 

class LLMClient:
    def __init__(self):
        self.api_key = os.getenv("LLM_API_KEY")
        if not self.api_key or self.api_key == "YOUR_LOCAL_API_KEY_HERE":
            print("Warning: Real LLM_API_KEY not found. Operating in fallback mode.")
            self.client = None
        else:
            self.client = OpenAI(api_key=self.api_key)

    def analyze_query_for_tool(self, query: str, available_tools: dict) -> str:
        """
        Asks the LLM to decide which tool to use based on the user's query.
        Returns the string name of the tool.
        """
        if not self.client:
            # Fallback logic if API key isn't set yet
            if "budget" in query.lower(): return "budget"
            if "summary" in query.lower(): return "summary"
            return None

        prompt = f"""
        You are a financial AI agent routing a user query to the correct tool.
        User Query: "{query}"
        Available Tools: {json.dumps(available_tools)}
        
        Respond ONLY with the exact key name of the tool to use. If no tool is needed, respond with 'NONE'.
        """
        try:
            response = self.client.chat.completions.create(
                model="gpt-3.5-turbo",
                messages=[{"role": "user", "content": prompt}],
                temperature=0
            )
            tool_name = response.choices[0].message.content.strip()
            return tool_name if tool_name in available_tools else None
        except Exception as e:
            print(f"LLM Routing Error: {e}")
            return None

    def generate_final_response(self, query: str, context_data: str) -> str:
        """
        Generates the final natural language response using the tool's raw data.
        """
        if not self.client:
            return f"[Simulated Output] Based on '{context_data}', your finance request '{query}' has been processed."

        prompt = f"""
        You are a helpful personal finance assistant. 
        User Query: "{query}"
        System Data context: "{context_data}"
        
        Provide a friendly, concise, and helpful response to the user incorporating the system data.
        """
        try:
            response = self.client.chat.completions.create(
                model="gpt-3.5-turbo",
                messages=[{"role": "user", "content": prompt}],
                temperature=0.7
            )
            return response.choices[0].message.content.strip()
        except Exception as e:
            return f"Error generating response: {e}"
