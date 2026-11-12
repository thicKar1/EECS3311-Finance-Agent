import java.util.Scanner;

public class ApplicationFacade {
    private FinanceAgent agent;

    public ApplicationFacade() {
        this.agent = new FinanceAgent();
    }

    public void runCLI() {
        System.out.println("==========================================");
        System.out.println("   Personal Finance AI Agent - CLI Mode   ");
        System.out.println("==========================================");
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();

        while (true) {
            System.out.print("\nAsk a finance question (or type 'reset' / 'exit'): ");
            String query = scanner.nextLine();
            if (query.equalsIgnoreCase("exit")) {
                System.out.println("Exiting CLI mode. Goodbye!");
                break;
            }
            if (query.equalsIgnoreCase("reset")) {
                DataManager.getInstance().resetDatabase();
                System.out.println("\n[System]: Database reset! All transactions and custom budget limits have been cleared.");
                continue;
            }
            if (query.trim().isEmpty()) {
                continue;
            }
            String response = agent.processQuery(userId, query);
            System.out.println("\n[Agent Response]:\n" + response);
        }
    }

    public void runGUI() {
        // Swing GUI startup
        javax.swing.SwingUtilities.invokeLater(() -> {
            javax.swing.JFrame frame = new javax.swing.JFrame("Personal Finance AI Agent");
            frame.setSize(400, 300);
            frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
            
            javax.swing.JPanel panel = new javax.swing.JPanel();
            frame.add(panel);
            panel.add(new javax.swing.JLabel("Enter User ID:"));
            javax.swing.JTextField userField = new javax.swing.JTextField(20);
            panel.add(userField);

            panel.add(new javax.swing.JLabel("Ask Question:"));
            javax.swing.JTextField queryField = new javax.swing.JTextField(20);
            panel.add(queryField);

            javax.swing.JButton submitBtn = new javax.swing.JButton("Submit");
            panel.add(submitBtn);

            submitBtn.addActionListener(e -> {
                String uId = userField.getText();
                String q = queryField.getText();
                if (!uId.isEmpty() && !q.isEmpty()) {
                    String res = agent.processQuery(uId, q);
                    javax.swing.JOptionPane.showMessageDialog(frame, res);
                }
            });

            frame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        ApplicationFacade app = new ApplicationFacade();
        
        // Parse command line arguments properly
        boolean isCliMode = false;
        if (args.length > 0) {
            for (String arg : args) {
                if (arg.equalsIgnoreCase("--mode=cli") || arg.equalsIgnoreCase("cli")) {
                    isCliMode = true;
                    break;
                }
            }
        }

        if (isCliMode) {
            app.runCLI();
        } else {
            app.runGUI();
        }
    }
}
