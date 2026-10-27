import json
import os
from abc import ABC, abstractmethod

# Singleton Pattern: Ensure only one instance manages the data file
class DataManager:
    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super(DataManager, cls).__new__(cls)
            cls._instance.data_file = "finance_data.json"
            cls._instance._initialize_file()
        return cls._instance

    def _initialize_file(self):
        if not os.path.exists(self.data_file):
            with open(self.data_file, 'w') as f:
                json.dump({"transactions": [], "budgets": {}}, f)

    def save_transaction(self, user_id: str, amount: float, category: str):
        with open(self.data_file, 'r') as f:
            data = json.load(f)
        
        data["transactions"].append({"user_id": user_id, "amount": amount, "category": category})
        
        with open(self.data_file, 'w') as f:
            json.dump(data, f)
        
        return data["transactions"]

# Observer Pattern: Notify the system/user when a budget is exceeded
class Observer(ABC):
    @abstractmethod
    def update(self, message: str):
        pass

class AlertSystem(Observer):
    def update(self, message: str):
        # In a real app, this could trigger a GUI popup or send an email
        print(f"\n[ALERT NOTIFICATION] {message}\n")

class BudgetSubject:
    def __init__(self):
        self._observers = []

    def attach(self, observer: Observer):
        self._observers.append(observer)

    def notify(self, message: str):
        for observer in self._observers:
            observer.update(message)

    def check_budget_limit(self, current_spending: float, budget_limit: float):
        if current_spending > budget_limit:
            self.notify(f"Warning! You have exceeded your budget of ${budget_limit}. Current spending: ${current_spending}")
