import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * Musicdroid - Collaborative Music Experience Prototype
 * 
 * A Java Swing GUI demonstrating the core features of Musicdroid:
 * 1. Session Management (Login/Join)
 * 2. Shared Playlist/Queue
 * 3. Simulated Synchronized Playback
 * 4. Real-time Chat Interface
 * 
 * @author Team Musicdroid
 */
public class MusicdroidApp extends JFrame {

    // Color Palette (Dark Mode)
    private static final Color BG_DARK = new Color(18, 18, 18);
    private static final Color BG_PANEL = new Color(33, 33, 33);
    private static final Color ACCENT_PURPLE = new Color(187, 134, 252);
    private static final Color ACCENT_TEAL = new Color(3, 218, 197);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(176, 176, 176);

    private CardLayout cardLayout;
    private JPanel mainPanel;

    // UI Components for "State"
    private DefaultListModel<String> playlistModel;
    private JTextArea chatArea;
    private JProgressBar syncProgressBar;
    private JLabel nowPlayingLabel;
    private Timer playbackTimer;
    private int currentSongProgress = 0;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new MusicdroidApp().setVisible(true);
        });
    }

    public MusicdroidApp() {
        setTitle("Musicdroid: Collaborative Listening");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createSessionPanel(), "SESSION");

        add(mainPanel);
    }

    /**
     * SCREEN 1: Login / Join Session
     */
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_DARK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Logo / Title
        JLabel title = new JLabel("Musicdroid", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 48));
        title.setForeground(ACCENT_PURPLE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        JLabel subtitle = new JLabel("Uniting friends through shared soundscapes.", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(TEXT_SECONDARY);
        gbc.gridy = 1;
        panel.add(subtitle, gbc);

        // Inputs
        JTextField userField = createStyledTextField("Enter Username");
        JTextField sessionField = createStyledTextField("Enter Session ID (e.g., JAM-123)");

        gbc.gridy = 2; gbc.insets = new Insets(40, 10, 10, 10);
        panel.add(userField, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(sessionField, gbc);

        // Join Button
        JButton joinBtn = createStyledButton("Join Session", ACCENT_TEAL);
        joinBtn.addActionListener(e -> {
            if (!userField.getText().isEmpty()) {
                cardLayout.show(mainPanel, "SESSION");
                startSimulation(); // Start the fake sync timer
            }
        });

        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(joinBtn, gbc);

        return panel;
    }

    /**
     * SCREEN 2: Main Session (Player, Chat, Queue)
     */
    private JPanel createSessionPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- LEFT: Playlist/Queue ---
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(BG_PANEL);
        leftPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        leftPanel.setPreferredSize(new Dimension(300, 0));

        JLabel queueLabel = new JLabel("Shared Queue");
        queueLabel.setForeground(TEXT_PRIMARY);
        queueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        playlistModel = new DefaultListModel<>();
        playlistModel.addElement("1. Midnight City - M83 (Added by Arnav)");
        playlistModel.addElement("2. Blinding Lights - The Weeknd (Added by Harsh)");
        playlistModel.addElement("3. Levitating - Dua Lipa (Added by Abhay)");

        JList<String> playlist = new JList<>(playlistModel);
        playlist.setBackground(BG_PANEL);
        playlist.setForeground(TEXT_SECONDARY);
        playlist.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        playlist.setFixedCellHeight(30);

        JButton addSongBtn = createStyledButton("+ Add Song", ACCENT_PURPLE);
        addSongBtn.addActionListener(e -> {
            String song = JOptionPane.showInputDialog(this, "Enter Song Name:");
            if (song != null && !song.isEmpty()) {
                playlistModel.addElement((playlistModel.getSize() + 1) + ". " + song + " (Added by You)");
            }
        });

        leftPanel.add(queueLabel, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(playlist), BorderLayout.CENTER);
        leftPanel.add(addSongBtn, BorderLayout.SOUTH);

        // --- CENTER: Visualizer & Controls ---
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BG_DARK);

        JPanel visualizer = new JPanel();
        visualizer.setBackground(Color.BLACK);
        visualizer.setBorder(new LineBorder(ACCENT_PURPLE, 2));
        JLabel artLabel = new JLabel("<html><center>♫<br>Visualizer Active<br>Synced with 3 Users</center></html>", SwingConstants.CENTER);
        artLabel.setForeground(ACCENT_PURPLE);
        artLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        visualizer.add(artLabel);

        JPanel controlsPanel = new JPanel(new GridLayout(2, 1));
        controlsPanel.setBackground(BG_DARK);

        nowPlayingLabel = new JLabel("Now Playing: Midnight City", SwingConstants.CENTER);
        nowPlayingLabel.setForeground(TEXT_PRIMARY);
        nowPlayingLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        syncProgressBar = new JProgressBar(0, 100);
        syncProgressBar.setForeground(ACCENT_TEAL);
        syncProgressBar.setBackground(Color.DARK_GRAY);
        syncProgressBar.setStringPainted(true);

        controlsPanel.add(nowPlayingLabel);
        controlsPanel.add(syncProgressBar);

        centerPanel.add(visualizer, BorderLayout.CENTER);
        centerPanel.add(controlsPanel, BorderLayout.SOUTH);

        // --- RIGHT: Chat ---
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(BG_PANEL);
        rightPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        rightPanel.setPreferredSize(new Dimension(250, 0));

        JLabel chatLabel = new JLabel("Session Chat");
        chatLabel.setForeground(TEXT_PRIMARY);
        chatLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBackground(BG_PANEL);
        chatArea.setForeground(TEXT_SECONDARY);
        chatArea.setLineWrap(true);
        chatArea.append("System: Connected to Sync Server.\n");
        chatArea.append("Arnav: Hey everyone! Ready to jam?\n");
        chatArea.append("Harsh: Audio sync is perfect 👌\n");

        JTextField chatInput = new JTextField();
        chatInput.addActionListener(e -> {
            String msg = chatInput.getText();
            if (!msg.isEmpty()) {
                chatArea.append("You: " + msg + "\n");
                chatInput.setText("");
            }
        });

        rightPanel.add(chatLabel, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        rightPanel.add(chatInput, BorderLayout.SOUTH);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private void startSimulation() {
        playbackTimer = new Timer(1000, e -> {
            currentSongProgress += 2;
            if (currentSongProgress > 100) currentSongProgress = 0;
            syncProgressBar.setValue(currentSongProgress);
            syncProgressBar.setString("Synced: " + (currentSongProgress * 3) + "s / 300s");
        });
        playbackTimer.start();
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField tf = new JTextField(placeholder, 20);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return tf;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return btn;
    }
}