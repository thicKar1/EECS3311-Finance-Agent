# Personal Finance AI Agent (EECS 3311 — Stage 2)

**Author:** Karthik Kumaran (`220162491`)

A Java-based Personal Finance AI Agent featuring a multi-step agentic tool-routing loop, OpenRouter LLM integration, CSV persistence, and both **Java Swing GUI** and **Command-Line Interface (CLI)** execution modes.

## Demonstration Video

* **Video Link:** [Watch Demo Recording on Google Drive](https://drive.google.com/file/d/143mHo5AnIn51OH96z8g8egOXr_S5bnVI/view?usp=sharing)

### Verified Features (`F01`–`F10`)

| Feature ID & Name | Mode | Sample Prompt |
| :--- | :--- | :--- |
| **F01:** Budget Management | GUI | `Set my budget limit to 1000` |
| **F02:** Transaction Entry & Observer Alert | GUI | `Log $250 for Groceries` / `Log $900 for Electronics` |
| **F03:** Spending Summary | GUI | `Show my spending summary` |
| **F04:** Anomaly Detection | GUI | `Check for unusual spending` |
| **F05:** Savings Goal Tracking | GUI | `Deposit 500 into my savings goal` |
| **F06:** Tax Estimator | GUI | `Estimate tax on 75000 income` |
| **F07:** Debt Management & Validation | GUI | `Pay 250 per month toward my debt` |
| **F08:** Currency Conversion | GUI | `Convert 250 USD to CAD` |
| **F09:** Investment Tracking | GUI | `Invest 2000 into my portfolio` |
| **F10:** Bill Reminders & CLI Demo | GUI & CLI | `Show my upcoming bills` (`--mode=cli`) |

## Architecture & Design Patterns

* **Facade Pattern (`ApplicationFacade.java`):** Unified entry point (`submitQuery`, `runGUI`, `runCLI`) decoupling user interfaces from agent routing and persistence.
* **Factory Method Pattern (`ToolFactory.java`, `FinanceTool.java`):** Dynamically instantiates the requested financial tool (`BudgetTool`, `TransactionTool`, `SummaryTool`, `AnomalyTool`, `GoalTool`, `TaxTool`, `DebtTool`, `CurrencyTool`, `InvestmentTool`, `BillTool`).
* **Observer Pattern (`BudgetTracker.java`, `BudgetObserver.java`, `AlertNotifier.java`):** Automatically triggers threshold alerts when cumulative user spending exceeds the configured budget limit.
* **Singleton Pattern (`DataManager.java`):** Centralizes thread-safe CSV persistence (`finance_transactions.csv`) across tools and interface modes.

## Prerequisites & Local Setup

1. **Java Development Kit (JDK 17 or 21+)** installed and available on your system `PATH`.
2. **Configure Environment Variables:**
   Copy `.env.example` to `.env` in the project root and add your OpenRouter API key (never commit `.env` to GitHub):

   ```bash
   cp .env.example .env
   ```

   Edit `.env`:

   ```env
   LLM_API_KEY=your_openrouter_api_key_here
   LLM_MODEL=openrouter/free
   ```

## Build & Run Instructions

Compile all Java source files from the project root directory:

```bash
javac *.java
```

### Run in GUI Mode (Default)

```bash
java ApplicationFacade
```

### Run in CLI Mode

```bash
java ApplicationFacade --mode=cli
```

## Project Structure

```text
├── ApplicationFacade.java   # GUI & CLI entry point (Facade)
├── FinanceAgent.java        # Two-step LLM tool-routing & response agent
├── LLMClient.java           # OpenRouter API client with HTTP 429 fallback
├── ToolFactory.java         # Tool instantiation factory
├── FinanceTool.java         # Common tool interface
├── DataManager.java         # CSV persistence singleton (finance_transactions.csv)
├── BudgetTracker.java       # Observable budget state
├── BudgetObserver.java      # Observer interface
├── AlertNotifier.java       # Budget threshold alert observer
├── BudgetTool.java          # F01: Budget management
├── TransactionTool.java     # F02: Expense logging & validation
├── SummaryTool.java         # F03: Category spending breakdown
├── AnomalyTool.java         # F04: Unusual spending detection (>2x average)
├── GoalTool.java            # F05: Savings goal deposits & progress
├── TaxTool.java             # F06: Progressive income tax estimation
├── DebtTool.java            # F07: Debt payoff & interest amortization
├── CurrencyTool.java        # F08: Multi-currency conversion
├── InvestmentTool.java      # F09: Portfolio contributions & 1-year projection
├── BillTool.java            # F10: Upcoming bill reminders
├── .env.example             # Environment variable template (safe to commit)
├── .gitignore               # Excludes .env, *.class, and runtime CSV files
├── stage2-report.pdf        # Stage 2 implementation report
└── README.md                # Setup, usage, and demo documentation
```