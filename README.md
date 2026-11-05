# EECS 3311 - Personal Finance AI Agent (Stage 2)

## Project Overview
This project is an intelligent personal finance assistant built in Java. It utilizes a ReAct (Reason + Act) architecture, allowing an LLM to autonomously route user queries to the appropriate local financial tools, fetch data, and synthesize natural language responses.

## Implemented Design Patterns (5 Required)
1. **Strategy Pattern:** Implemented via the `FinanceTool` interface. Allows the agent to execute any tool interchangeably without knowing its specific implementation.
2. **Facade Pattern:** Implemented via `ApplicationFacade`. Provides a unified, simplified startup interface handling both CLI and GUI modes.
3. **Singleton Pattern:** Implemented via `DataManager`. Ensures only a single, globally accessible instance manages file read/write operations to prevent data conflicts.
4. **Factory Pattern:** Implemented via `ToolFactory`. Centralizes the instantiation logic for the 10 distinct financial tools based on LLM routing decisions.
5. **Observer Pattern:** Implemented via `BudgetTracker` (Subject), `BudgetObserver` (Interface), and `AlertNotifier` (Concrete Observer). Decouples the budget limit checking logic from the alerting system.

## Financial Features (10 Required)
1. **Budget Management:** Fetches budget status and limits.
2. **Transaction Entry:** Saves new transactions to local storage.
3. **Summaries:** Calculates total spending over a given period.
4. **Anomaly Detection:** Flags unusual spending spikes.
5. **Goal Tracking:** Tracks progress toward savings goals.
6. **Tax Estimator:** Calculates estimated tax brackets and deductions.
7. **Debt Management:** Tracks debts and calculates payoff schedules.
8. **Currency Conversion:** Handles currency exchange math.
9. **Investment Tracking:** Monitors portfolio performance.
10. **Bill Reminders:** Manages upcoming bill due dates.

## Architecture & ReAct Loop
The `FinanceAgent` integrates with `LLMClient` to perform a true multi-step reasoning loop:
* **Reason:** The LLM analyzes the user query and selects a tool from the `ToolFactory`.
* **Act:** The agent executes the selected tool (e.g., `TransactionTool`) to fetch or modify local data.
* **Synthesize:** The LLM generates a final, contextual response using the tool's output.
