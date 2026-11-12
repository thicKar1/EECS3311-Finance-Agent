import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class ApplicationFacade {
    private final FinanceAgent agent;

    public ApplicationFacade() {
        this.agent = new FinanceAgent();
    }

    public String submitQuery(String userId, String query) {
        if (userId == null || userId.trim().isEmpty()) {
            userId = "user1";
        }
        if (query == null || query.trim().isEmpty()) {
            return "Please enter a finance question.";
        }
        if (query.trim().equalsIgnoreCase("reset")) {
            DataManager.getInstance().resetDatabase();
            return "Database reset! All transactions and custom budget limits have been cleared.";
        }
        return agent.processQuery(userId.trim(), query.trim());
    }

    public void runCLI() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("========================================");
        System.out.println("  Personal Finance AI Agent - CLI Mode  ");
        System.out.println("========================================");
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine().trim();
        if (userId.isEmpty()) {
            userId = "user1";
        }

        while (true) {
            System.out.print("\nAsk a finance question (or type 'reset' / 'exit'): ");
            String query = scanner.nextLine().trim();

            if (query.equalsIgnoreCase("exit")) {
                System.out.println("Exiting Personal Finance AI Agent. Goodbye!");
                break;
            }

            if (query.equalsIgnoreCase("reset")) {
                DataManager.getInstance().resetDatabase();
                System.out.println("\n[System]: Database reset! All transactions and custom budget limits have been cleared.");
                continue;
            }

            String response = agent.processQuery(userId, query);
            System.out.println("\n[Agent Response]:");
            System.out.println(response);
        }
        scanner.close();
    }

    public void runGUI() {
        JFrame frame = new JFrame("Personal Finance AI Agent");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 200);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel userLabel = new JLabel("Enter User ID:");
        JTextField userField = new JTextField("user1");

        JLabel queryLabel = new JLabel("Ask Question:");
        JTextField queryField = new JTextField();

        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(e -> {
            String userId = userField.getText().trim();
            String query = queryField.getText().trim();
            String response = submitQuery(userId, query);
            JOptionPane.showMessageDialog(frame, response);
        });

        panel.add(userLabel);
        panel.add(userField);
        panel.add(queryLabel);
        panel.add(queryField);
        panel.add(new JLabel());
        panel.add(submitButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        ApplicationFacade facade = new ApplicationFacade();
        boolean cliMode = false;
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--mode=cli")) {
                cliMode = true;
                break;
            }
        }

        if (cliMode) {
            facade.runCLI();
        } else {
            SwingUtilities.invokeLater(facade::runGUI);
        }
    }
}