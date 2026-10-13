import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Random;

public class RockPaperScissorsFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final String[] MOVES = {"R", "P", "S"};

    private final Random random = new Random();

    private final int[] playerMoveCounts = new int[3];
    private String lastPlayerMove;

    private int playerWins;
    private int computerWins;
    private int ties;



    private final JTextField playerWinsField = createCounterField();
    private final JTextField computerWinsField = createCounterField();
    private final JTextField tiesField = createCounterField();
    private final JTextArea resultsArea = new JTextArea(15, 65);

    private final Strategy cheatStrategy = new Cheat();
    private final Strategy randomStrategy = new RandomStrategy();
    private final Strategy leastUsedStrategy = new LeastUsed();
    private final Strategy mostUsedStrategy = new MostUsed();
    private final Strategy lastUsedStrategy = new LastUsed();


    public RockPaperScissorsFrame() {
        super("Rock Paper Scissors Game");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel(
                "Rock Paper Scissors", SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));

        JPanel heading = new JPanel(new BorderLayout(10, 10));
        heading.add(title, BorderLayout.NORTH);
        heading.add(createStatsPanel(), BorderLayout.CENTER);

        content.add(heading, BorderLayout.NORTH);
        content.add(createResultsPanel(), BorderLayout.CENTER);
        content.add(createButtonPanel(), BorderLayout.SOUTH);

        setContentPane(content);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    private JTextField createCounterField() {
        JTextField field = new JTextField("0", 5);
        field.setEditable(false);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        return field;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 12, 6));
        panel.setBorder(BorderFactory.createTitledBorder(
                "Session Statistics"));

        panel.add(new JLabel("Player Wins", SwingConstants.CENTER));
        panel.add(new JLabel("Computer Wins", SwingConstants.CENTER));
        panel.add(new JLabel("Ties", SwingConstants.CENTER));

        panel.add(playerWinsField);
        panel.add(computerWinsField);
        panel.add(tiesField);

        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                "Round History"));

        resultsArea.setEditable(false);
        resultsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        resultsArea.setMargin(new Insets(8, 8, 8, 8));

        JScrollPane scrollPane = new JScrollPane(resultsArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setBorder(BorderFactory.createTitledBorder(
                "Choose Your Move"));

        JButton rockButton = createButton("Rock", "rock.jpg");
        JButton paperButton = createButton("Paper", "paper.png");
        JButton scissorsButton = createButton(
                "Scissors", "scissor.png");
        JButton quitButton = createButton("Quit", "cross.jpg");

        rockButton.setActionCommand("R");
        paperButton.setActionCommand("P");
        scissorsButton.setActionCommand("S");

        ActionListener moveListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                playRound(ae.getActionCommand());
            }
        };

        rockButton.addActionListener(moveListener);
        paperButton.addActionListener(moveListener);
        scissorsButton.addActionListener(moveListener);

        quitButton.addActionListener(ae -> System.exit(0));

        panel.add(rockButton);
        panel.add(paperButton);
        panel.add(scissorsButton);
        panel.add(quitButton);

        return panel;
    }

    private JButton createButton(String text, String imageName) {
        URL imageUrl = getClass().getResource("/" + imageName);

        if (imageUrl == null) {
            throw new IllegalStateException(
                    "Missing image: " + imageName
                            + ". Put it in src and rebuild the project.");
        }

        ImageIcon originalIcon = new ImageIcon(imageUrl);

        int originalWidth = originalIcon.getIconWidth();
        int originalHeight = originalIcon.getIconHeight();

        if (originalWidth <= 0 || originalHeight <= 0) {
            throw new IllegalStateException(
                    "Unable to load image: " + imageName);
        }

        final int iconSize = 80;

        double scale = Math.min(
                (double) iconSize / originalWidth,
                (double) iconSize / originalHeight);

        int scaledWidth = Math.max(
                1, (int) Math.round(originalWidth * scale));
        int scaledHeight = Math.max(
                1, (int) Math.round(originalHeight * scale));

        BufferedImage iconImage = new BufferedImage(
                iconSize, iconSize, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = iconImage.createGraphics();

        try {
            graphics.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            graphics.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            int x = (iconSize - scaledWidth) / 2;
            int y = (iconSize - scaledHeight) / 2;

            graphics.drawImage(
                    originalIcon.getImage(),
                    x, y, scaledWidth, scaledHeight, null);
        } finally {
            graphics.dispose();
        }

        JButton button = new JButton(text, new ImageIcon(iconImage));
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setIconTextGap(8);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setMargin(new Insets(10, 10, 10, 10));

        return button;
    }

    private void playRound(String playerMove) {
        int probability = random.nextInt(100) + 1;

        Strategy selectedStrategy;
        String strategyName;

        if (probability <= 10) {
            selectedStrategy = cheatStrategy;
            strategyName = "Cheat";
        } else if (probability <= 30) {
            selectedStrategy = leastUsedStrategy;
            strategyName = "Least Used";
        } else if (probability <= 50) {
            selectedStrategy = mostUsedStrategy;
            strategyName = "Most Used";
        } else if (probability <= 70) {
            if (lastPlayerMove == null) {
                selectedStrategy = randomStrategy;
                strategyName = "Random - first-round fallback";
            } else {
                selectedStrategy = lastUsedStrategy;
                strategyName = "Last Used";
            }
        } else {
            selectedStrategy = randomStrategy;
            strategyName = "Random";
        }

        String computerMove = selectedStrategy.getMove(playerMove);
        String result;

        if (playerMove.equals(computerMove)) {
            ties++;
            result = "Both chose " + moveName(playerMove)
                    + ". (Tie! Computer: " + strategyName + ")";
        } else if (winningMove(computerMove).equals(playerMove)) {
            playerWins++;
            result = winningDescription(playerMove)
                    + ". (Player wins! Computer: "
                    + strategyName + ")";
        } else {
            computerWins++;
            result = winningDescription(computerMove)
                    + ". (Computer wins! Computer: "
                    + strategyName + ")";
        }

        playerMoveCounts[moveIndex(playerMove)]++;
        lastPlayerMove = playerMove;

        playerWinsField.setText(Integer.toString(playerWins));
        computerWinsField.setText(Integer.toString(computerWins));
        tiesField.setText(Integer.toString(ties));

        int roundNumber = playerWins + computerWins + ties;
        resultsArea.append(roundNumber + ". " + result + "\n");
        resultsArea.setCaretPosition(
                resultsArea.getDocument().getLength());
    }

    private int moveIndex(String move) {
        switch (move) {
            case "R":
                return 0;
            case "P":
                return 1;
            case "S":
                return 2;
            default:
                throw new IllegalArgumentException(
                        "Invalid move: " + move);
        }
    }

    private String winningMove(String move) {
        switch (move) {
            case "R":
                return "P";
            case "P":
                return "S";
            case "S":
                return "R";
            default:
                throw new IllegalArgumentException(
                        "Invalid move: " + move);
        }
    }

    private String moveName(String move) {
        switch (move) {
            case "R":
                return "Rock";
            case "P":
                return "Paper";
            case "S":
                return "Scissors";
            default:
                throw new IllegalArgumentException(
                        "Invalid move: " + move);
        }
    }

    private String winningDescription(String winningMove) {
        switch (winningMove) {
            case "R":
                return "Rock breaks scissors";
            case "P":
                return "Paper covers rock";
            case "S":
                return "Scissors cuts paper";
            default:
                throw new IllegalArgumentException(
                        "Invalid move: " + winningMove);
        }
    }

    private String selectHistoricalMove(boolean least) {
        int targetCount = playerMoveCounts[0];

        for (int count : playerMoveCounts) {
            if (least) {
                targetCount = Math.min(targetCount, count);
            } else {
                targetCount = Math.max(targetCount, count);
            }
        }

        int[] candidates = new int[MOVES.length];
        int candidateCount = 0;

        for (int i = 0; i < playerMoveCounts.length; i++) {
            if (playerMoveCounts[i] == targetCount) {
                candidates[candidateCount++] = i;
            }
        }

        int selectedIndex = candidates[random.nextInt(candidateCount)];
        return MOVES[selectedIndex];
    }

    private class LeastUsed implements Strategy {

        @Override
        public String getMove(String playerMove) {
            return winningMove(selectHistoricalMove(true));
        }
    }

    private class MostUsed implements Strategy {
        @Override
        public String getMove(String playerMove) {
            return winningMove(selectHistoricalMove(false));
        }
    }

    private class LastUsed implements Strategy {
        @Override
        public String getMove(String playerMove) {
            if (lastPlayerMove == null) {
                throw new IllegalStateException(
                        "Last Used requires a previous round.");
            }

            return lastPlayerMove;
        }
    }
}