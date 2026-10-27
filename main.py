import argparse
import tkinter as tk
from tkinter import messagebox
from agent_core import FinanceAgent

# Facade Pattern: A unified interface to start the application in different modes
class ApplicationFacade:
    def __init__(self):
        self.agent = FinanceAgent()

    def run_cli(self):
        print("--- Personal Finance Agent CLI ---")
        user_id = input("Enter User ID: ")
        while True:
            query = input("Ask a question (or type 'exit'): ")
            if query.lower() == 'exit':
                break
            response = self.agent.process_query(user_id, query)
            print(f"Agent: {response}\n")

    def run_gui(self):
        root = tk.Tk()
        root.title("Personal Finance Agent")
        root.geometry("400x300")

        tk.Label(root, text="Enter User ID:").pack(pady=5)
        user_entry = tk.Entry(root)
        user_entry.pack(pady=5)

        tk.Label(root, text="Ask a question:").pack(pady=5)
        query_entry = tk.Entry(root, width=40)
        query_entry.pack(pady=5)

        def on_submit():
            user_id = user_entry.get()
            query = query_entry.get()
            if not user_id or not query:
                messagebox.showwarning("Input Error", "Please enter both User ID and a query.")
                return
            response = self.agent.process_query(user_id, query)
            messagebox.showinfo("Agent Response", response)

        tk.Button(root, text="Submit", command=on_submit).pack(pady=20)
        root.mainloop()

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Run the Finance Agent")
    parser.add_argument('--mode', choices=['cli', 'gui'], default='gui', help="Choose the interface mode")
    args = parser.parse_args()

    app = ApplicationFacade()
    if args.mode == 'cli':
        app.run_cli()
    else:
        app.run_gui()
