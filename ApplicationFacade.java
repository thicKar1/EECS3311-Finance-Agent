import javax.swing.*;
import java.util.Scanner;

public class ApplicationFacade {
    private FinanceAgent agent;

    public ApplicationFacade() {
        this.agent = new FinanceAgent();
    }

    public void runCLI() {
        System.out.println("--- Personal Finance Agent CLI ---");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();
        
        while (true) {
            System.out.print("Ask a question (or type 'exit'): ");
            String query = scanner.nextLine();
            if (query.equalsIgnoreCase("exit")) break;
            
            String response = agent.processQuery(userId, query);
            System.out.println("Agent: " + response + "\n");
        }
        scanner.close();
    }

    public void runGUI() {
        JFrame frame = new JFrame("Personal Finance Agent");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel userLabel = new JLabel("Enter User ID:");
        userLabel.setBounds(20, 20, 100, 25);
        frame.add(userLabel);

        JTextField userEntry = new JTextField();
        userEntry.setBounds(130, 20, 200, 25);
        frame.add(userEntry);

        JLabel queryLabel = new JLabel("Ask a question:");
        queryLabel.setBounds(20, 60, 100, 25);
        frame.add(queryLabel);

        JTextField queryEntry = new JTextField();
        queryEntry.setBounds(130, 60, 200, 25);
        frame.add(queryEntry);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(130, 100, 100, 30);
        frame.add(submitButton);

        submitButton.addActionListener(e -> {
            String userId = userEntry.getText();
            String query = queryEntry.getText();
            if (userId.isEmpty() || query.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter both User ID and a query.");
                return;
            }
            String response = agent.processQuery(userId, query);
            JOptionPane.showMessageDialog(frame, response, "Agent Response", JOptionPane.INFORMATION_MESSAGE);
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        ApplicationFacade app = new ApplicationFacade();
        // You can swap to runCLI() for terminal testing
        app.runGUI(); 
    }
}
