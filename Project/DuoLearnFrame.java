package Project;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DuoLearnFrame extends JFrame {

    private final AppData data;
    private final LearningService service;

    private JLabel xpValue;
    private JLabel accuracyValue;
    private JLabel lessonValue;

    private JLabel quizWord;
    private JLabel quizFeedback;
    private JPanel choicePanel;
    private JPanel dashboardCards;
    private CardLayout dashboardLayout;

    private String selectedCategory;
    private JPanel mainPages;
    private CardLayout mainPageLayout;
    private boolean darkMode = false;
    private boolean questionScored = false;

    // สีตัวอักษรของปุ่ม
    private static final Color BUTTON_TEXT =
            new Color(40, 55, 65);

    // ฟอนต์หลัก
    private static final Font BUTTON_FONT =
            new Font("Tahoma", Font.BOLD, 15);

    public DuoLearnFrame() {

        data = DataStore.load();
        service = new LearningService(data);

        setTitle("DuoLinguini | Learn a little every day");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);

        // ตั้งค่าไอคอนโปรแกรม
        try {
            ImageIcon appIcon = new ImageIcon(
                    getClass().getResource("/Project/logo.jpg")
            );

            setIconImage(appIcon.getImage());

        } catch (Exception e) {
            System.out.println("ไม่พบไฟล์โลโก้");
        }

        getContentPane().setBackground(UITheme.CREAM);
        setLayout(new BorderLayout());

        // ตั้งค่าฟอนต์ภาษาไทยสำหรับหน้าต่างแจ้งเตือน
        UIManager.put(
                "OptionPane.messageFont",
                new Font("Tahoma", Font.PLAIN, 14)
        );

        UIManager.put(
                "OptionPane.buttonFont",
                new Font("Tahoma", Font.BOLD, 14)
        );

        add(createHeader(), BorderLayout.NORTH);
        add(createMainNavigation(), BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                DataStore.save(data);
            }
        });

        refreshStats();
    }

    // ================= HEADER =================

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());

        header.setBackground(Color.WHITE);
        header.setBorder(
                new EmptyBorder(14, 22, 14, 22)
        );

        JLabel brand = new JLabel("●  DuoLinguini");

        brand.setFont(
                new Font("Tahoma", Font.BOLD, 26)
        );

        brand.setForeground(UITheme.GREEN);

        JLabel tagline = new JLabel(
                "Learning something every day."
        );

        tagline.setFont(
                new Font("Tahoma", Font.PLAIN, 13)
        );

        tagline.setForeground(UITheme.MUTED);

        JPanel brandPanel = new JPanel(
                new GridLayout(2, 1, 0, 2)
        );

        brandPanel.setOpaque(false);
        brandPanel.add(brand);
        brandPanel.add(tagline);

        JLabel xp = new JLabel(
                "★ " + data.getXp() + " XP"
        );

        xp.setFont(
                new Font("Segoe UI Symbol", Font.BOLD, 17)
        );

        xp.setForeground(UITheme.ORANGE);

        header.add(brandPanel, BorderLayout.WEST);
        header.add(xp, BorderLayout.EAST);

        return header;
    }

    // ================= MAIN NAVIGATION =================

    // ใช้แถบไอคอนด้านล่างแทนแท็บข้อความ
    private JPanel createMainNavigation() {

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.CREAM);

        mainPageLayout = new CardLayout();
        mainPages = new JPanel(mainPageLayout);
        mainPages.setBackground(UITheme.CREAM);

        mainPages.add(makeScrollable(createDashboard()), "home");
        mainPages.add(makeScrollable(createVocabulary()), "vocabulary");
        mainPages.add(makeScrollable(createReview()), "review");
        mainPages.add(makeScrollable(createHistory()), "history");

        JPanel navigation = new JPanel(new FlowLayout(FlowLayout.CENTER, 55, 8));
        navigation.setBackground(Color.WHITE);
        navigation.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER));

        JButton homeButton = createNavigationIcon("⌂", "Home");
        homeButton.addActionListener(e -> mainPageLayout.show(mainPages, "home"));

        JButton moreButton = createNavigationIcon("⋯", "Menu");
        moreButton.addActionListener(e -> showMoreMenu(moreButton));

        navigation.add(homeButton);
        navigation.add(moreButton);

        root.add(mainPages, BorderLayout.CENTER);
        root.add(navigation, BorderLayout.SOUTH);
        return root;
    }

    private JButton createNavigationIcon(String iconText, String tooltip) {
        JButton button = new JButton(iconText);
        button.setFont(new Font("Segoe UI Symbol", Font.BOLD, 31));
        button.setForeground(UITheme.DARK_GREEN);
        button.setBackground(Color.WHITE);
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(72, 48));
        button.setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 12));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showMoreMenu(Component anchor) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem vocabularyItem = new JMenuItem("Vocabulary");
        JMenuItem reviewItem = new JMenuItem("Review vocabulary");
        JMenuItem historyItem = new JMenuItem("Log / History");
        JMenuItem settingsItem = new JMenuItem("⚙  Setting");

        Font iconFont = new Font("Segoe UI Symbol", Font.PLAIN, 15);

        vocabularyItem.setFont(new Font("Tahoma", Font.PLAIN, 15));
        reviewItem.setFont(new Font("Tahoma", Font.PLAIN, 15));
        historyItem.setFont(new Font("Tahoma", Font.PLAIN, 15));
        settingsItem.setFont(iconFont);

        vocabularyItem.addActionListener(e -> mainPageLayout.show(mainPages, "vocabulary"));
        reviewItem.addActionListener(e -> mainPageLayout.show(mainPages, "review"));
        historyItem.addActionListener(e -> mainPageLayout.show(mainPages, "history"));
        settingsItem.addActionListener(e -> showSettingsDialog());

        menu.add(vocabularyItem);
        menu.add(reviewItem);
        menu.add(historyItem);
        menu.addSeparator();
        menu.add(settingsItem);
        menu.show(anchor, anchor.getWidth() / 2, -menu.getPreferredSize().height);
    }

    private void showSettingsDialog() {
        JDialog dialog = new JDialog(this, "Setting", true);
        dialog.setSize(360, 190);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(12, 12));

        JPanel content = new JPanel(new BorderLayout(12, 18));
        content.setBorder(new EmptyBorder(20, 22, 20, 22));
        content.setBackground(darkMode ? new Color(15, 26, 19) : UITheme.CREAM);

        JLabel title = new JLabel("Theme Layout");
        title.setFont(new Font("Tahoma", Font.BOLD, 18));
        title.setForeground(darkMode ? new Color(235, 245, 238) : UITheme.INK);

        JPanel switchRow = new JPanel(new BorderLayout(10, 0));
        switchRow.setOpaque(false);
        JLabel modeLabel = new JLabel("Dark mode");
        modeLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
        modeLabel.setForeground(darkMode ? new Color(235, 245, 238) : UITheme.INK);

        JToggleButton themeSwitch = new JToggleButton(darkMode ? "ON" : "OFF", darkMode);
        themeSwitch.setFont(new Font("Tahoma", Font.BOLD, 14));
        themeSwitch.setFocusPainted(false);
        themeSwitch.setPreferredSize(new Dimension(82, 34));
        themeSwitch.setSelected(darkMode);
        themeSwitch.addItemListener(e -> {
            darkMode = themeSwitch.isSelected();
            themeSwitch.setText(darkMode ? "ON" : "OFF");
            applyTheme(getContentPane());
            applyTheme(mainPages);
            applyTheme(content);
            applyTheme(dialog);

            content.setBackground(darkMode ? new Color(15, 26, 19) : UITheme.CREAM);
            title.setForeground(darkMode ? new Color(235, 245, 238) : UITheme.INK);
            modeLabel.setForeground(darkMode ? new Color(235, 245, 238) : UITheme.INK);

            revalidate();
            repaint();
        });

        switchRow.add(modeLabel, BorderLayout.WEST);
        switchRow.add(themeSwitch, BorderLayout.EAST);
        content.add(title, BorderLayout.NORTH);
        content.add(switchRow, BorderLayout.CENTER);
        dialog.add(content, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // เปลี่ยนสี component ที่มีอยู่ทันที โดยไม่รีเซ็ตบทเรียนหรือข้อมูล
    private void applyTheme(Component component) {
        if (component instanceof JComponent) {
            JComponent jc = (JComponent) component;
            Color bg = jc.getBackground();
            Color fg = jc.getForeground();
            if (bg != null) jc.setBackground(mapThemeBackground(bg));
            if (fg != null) jc.setForeground(mapThemeForeground(fg));
        } else {
            Color bg = component.getBackground();
            Color fg = component.getForeground();
            if (bg != null) component.setBackground(mapThemeBackground(bg));
            if (fg != null) component.setForeground(mapThemeForeground(fg));
        }

        if (component instanceof JTable) {
            JTable table = (JTable) component;
            table.setBackground(darkMode ? new Color(28, 43, 33) : Color.WHITE);
            table.setForeground(darkMode ? new Color(235, 245, 238) : UITheme.INK);
            table.setGridColor(darkMode ? new Color(54, 75, 60) : UITheme.BORDER);
            table.setSelectionBackground(darkMode ? new Color(38, 75, 49) : UITheme.PALE_GREEN);
            table.setSelectionForeground(darkMode ? Color.WHITE : UITheme.INK);

            table.getTableHeader().setBackground(darkMode ? new Color(38, 60, 45) : UITheme.PALE_GREEN);
            table.getTableHeader().setForeground(darkMode ? Color.WHITE : UITheme.INK);

        } else if (component instanceof JToggleButton) {
            JToggleButton toggle = (JToggleButton) component;
            toggle.setForeground(UITheme.INK);
        }

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                applyTheme(child);
            }
        }
    }

    private Color mapThemeBackground(Color color) {
        if (darkMode) {
            if (color.equals(Color.WHITE)) return new Color(28, 43, 33);
            if (color.equals(Color.BLACK)) return new Color(15, 26, 19);
            if (color.equals(UITheme.CREAM)) return new Color(15, 26, 19);
            if (color.equals(UITheme.PALE_GREEN)) return new Color(38, 60, 45);
            if (color.equals(UITheme.BORDER)) return new Color(54, 75, 60);
        } else {
            if (color.equals(new Color(28, 43, 33))) return Color.WHITE;
            if (color.equals(new Color(15, 26, 19))) return UITheme.CREAM;
            if (color.equals(new Color(38, 60, 45))) return UITheme.PALE_GREEN;
            if (color.equals(new Color(54, 75, 60))) return UITheme.BORDER;
        }
        return color;
    }

    private Color mapThemeForeground(Color color) {
        if (darkMode) {
            if (color.equals(Color.BLACK) || color.equals(Color.DARK_GRAY)
                    || color.equals(UITheme.INK) || color.equals(BUTTON_TEXT)
                    || color.equals(UITheme.DARK_GREEN)) {
                return new Color(235, 245, 238);
            }
            if (color.equals(UITheme.MUTED) || color.equals(Color.GRAY)) {
                return new Color(174, 192, 179);
            }
        } else {
            if (color.equals(new Color(235, 245, 238))) return UITheme.INK;
            if (color.equals(new Color(174, 192, 179))) return UITheme.MUTED;
        }
        return color;
    }

    // ทำให้หน้า GUI สามารถเลื่อนขึ้น-ลงได้
    private JScrollPane makeScrollable(JPanel content) {

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.getViewport().setBackground(UITheme.CREAM);

        return scrollPane;
    }

    // ================= PAGE =================

    private JPanel page() {

        JPanel panel = new JPanel(
                new BorderLayout(14, 14)
        );

        panel.setBackground(UITheme.CREAM);

        panel.setBorder(
                new EmptyBorder(22, 24, 22, 24)
        );

        return panel;
    }

    // ================= BUTTON STYLE =================

    private void styleDarkTextButton(
            JButton button,
            Color background
    ) {

        UITheme.styleButton(button, background);
        button.setFont(BUTTON_FONT);

        // ปรับแก้ไข: หากปุ่มเป็นสีอ่อน (เช่น PALE_GREEN) ให้ใช้ข้อความสีเข้มเสมอ
        if (background.equals(UITheme.PALE_GREEN) || background.equals(Color.WHITE)) {
            button.setForeground(BUTTON_TEXT);
        } else {
            button.setForeground(darkMode ? new Color(235, 245, 238) : BUTTON_TEXT);
        }

        button.setEnabled(true);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
    }

    // ================= DASHBOARD =================

    private JPanel createDashboard() {

        JPanel panel = page();
        panel.setLayout(new BorderLayout(14, 18));

        JPanel top = new JPanel(new GridLayout(1, 3, 14, 14));
        top.setOpaque(false);

        xpValue = statCard("Score points", data.getXp() + " XP", UITheme.ORANGE);
        accuracyValue = statCard("Correct!", accuracy() + "%", UITheme.GREEN);
        lessonValue = statCard("Lesson Ended", String.valueOf(data.getLessonsCompleted()), UITheme.BLUE);

        top.add(wrapStat("Score points", xpValue, UITheme.ORANGE));
        top.add(wrapStat("Accuracy", accuracyValue, UITheme.GREEN));
        top.add(wrapStat("Lesson Ended", lessonValue, UITheme.BLUE));

        dashboardLayout = new CardLayout();
        dashboardCards = new JPanel(dashboardLayout);
        dashboardCards.setOpaque(false);
        dashboardCards.add(createLessonSelection(), "lessons");
        dashboardCards.add(createQuizPanel(), "quiz");

        panel.add(top, BorderLayout.NORTH);
        panel.add(dashboardCards, BorderLayout.CENTER);
        dashboardLayout.show(dashboardCards, "lessons");

        return panel;
    }

    private JPanel createLessonSelection() {

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);

        JPanel intro = new JPanel();
        intro.setOpaque(false);
        intro.setLayout(new BoxLayout(intro, BoxLayout.Y_AXIS));

        JPanel introTop = new JPanel(new BorderLayout(12, 0));
        introTop.setOpaque(false);

        JPanel introText = new JPanel();
        introText.setOpaque(false);
        introText.setLayout(new BoxLayout(introText, BoxLayout.Y_AXIS));

        JLabel title = UITheme.heading("Course", 23);
        JLabel description = new JLabel("Select course to practice");
        description.setFont(new Font("Tahoma", Font.PLAIN, 15));
        description.setForeground(darkMode ? new Color(174, 192, 179) : UITheme.MUTED);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        description.setAlignmentX(Component.LEFT_ALIGNMENT);
        introText.add(title);
        introText.add(Box.createVerticalStrut(7));
        introText.add(description);

        JButton addCourseButton = new JButton("⊕");
        addCourseButton.setFont(new Font("Segoe UI Symbol", Font.BOLD, 30));
        addCourseButton.setForeground(UITheme.GREEN);
        addCourseButton.setBackground(Color.WHITE);
        addCourseButton.setOpaque(true);
        addCourseButton.setFocusPainted(false);
        addCourseButton.setBorder(BorderFactory.createLineBorder(UITheme.GREEN, 2, true));
        addCourseButton.setPreferredSize(new Dimension(48, 48));
        addCourseButton.setToolTipText("Add course");
        addCourseButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addCourseButton.addActionListener(e -> showAddCourseDialog());

        introTop.add(introText, BorderLayout.CENTER);
        introTop.add(addCourseButton, BorderLayout.EAST);
        intro.add(introTop);

        JPanel categoryPanel = new JPanel(new GridLayout(0, 2, 14, 14));
        categoryPanel.setOpaque(false);

        for (String category : service.getCategories()) {
            int wordCount = service.getWordsInCategory(category).size();
            JButton button = new JButton();
            button.setLayout(new BorderLayout());
            styleDarkTextButton(button, UITheme.PALE_GREEN);
            button.setPreferredSize(new Dimension(250, 112));
            button.setMinimumSize(new Dimension(180, 100));

            JPanel labels = new JPanel();
            labels.setOpaque(false);
            labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));

            JLabel categoryLabel = new JLabel(category);
            categoryLabel.setFont(fontForLanguageText(category, Font.BOLD, 17));
            categoryLabel.setForeground(darkMode ? new Color(235, 245, 238) : UITheme.DARK_GREEN);
            categoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel countLabel = new JLabel(wordCount + " Vocabulary");
            countLabel.setFont(new Font("Tahoma", Font.PLAIN, 13));
            countLabel.setForeground(darkMode ? new Color(174, 192, 179) : UITheme.MUTED);
            countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            labels.add(Box.createVerticalGlue());
            labels.add(categoryLabel);
            labels.add(Box.createVerticalStrut(7));
            labels.add(countLabel);
            labels.add(Box.createVerticalGlue());
            button.add(labels, BorderLayout.CENTER);
            button.addActionListener(e -> startSelectedLesson(category));
            categoryPanel.add(button);
        }

        content.add(intro, BorderLayout.NORTH);
        content.add(categoryPanel, BorderLayout.CENTER);
        return content;
    }

    // ใช้ฟอนต์ fallback เฉพาะข้อความที่มีอักษรญี่ปุ่น/เกาหลี/จีน
    // ส่วนข้อความภาษาไทยและอังกฤษยังคงใช้ Tahoma ตามเดิม
    private Font fontForLanguageText(String text, int style, int size) {
        if (text != null) {
            for (int i = 0; i < text.length(); ) {
                int cp = text.codePointAt(i);
                i += Character.charCount(cp);
                if ((cp >= 0x3040 && cp <= 0x30FF) || (cp >= 0x31F0 && cp <= 0x31FF)) {
                    return new Font("Yu Gothic UI", style, size);
                }
                if (cp >= 0xAC00 && cp <= 0xD7AF) {
                    return new Font("Malgun Gothic", style, size);
                }
                if ((cp >= 0x3400 && cp <= 0x4DBF) || (cp >= 0x4E00 && cp <= 0x9FFF)) {
                    return new Font("Microsoft YaHei", style, size);
                }
            }
        }
        return new Font("Tahoma", style, size);
    }

    private void showAddCourseDialog() {
        String[] languages = {
            "日本語 (Japanese)",
            "한국어 (Korean)",
            "中文 (Chinese)",
            "Español (Spanish)",
            "Français (French)",
            "Deutsch (German)"
        };

        // Dialog เป็น logical font ของ Java ที่รองรับการ fallback ไปยังฟอนต์
        // ของระบบปฏิบัติการสำหรับอักษรญี่ปุ่น เกาหลี และจีน
        UIManager.put("OptionPane.messageFont", new Font("Tahoma", Font.PLAIN, 15));
        UIManager.put("OptionPane.buttonFont", new Font("Tahoma", Font.BOLD, 14));
        UIManager.put("ComboBox.font", new Font("Dialog", Font.PLAIN, 15));

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "What would you like to learning?",
                "Add Course",
                JOptionPane.PLAIN_MESSAGE,
                null,
                languages,
                languages[0]
        );

        if (selected == null) return;

        if (data.addLanguageCourse(selected)) {
            DataStore.save(data);
            dashboardCards.removeAll();
            dashboardCards.add(createLessonSelection(), "lessons");
            dashboardCards.add(createQuizPanel(), "quiz");
            dashboardLayout.show(dashboardCards, "lessons");
            dashboardCards.revalidate();
            dashboardCards.repaint();
            JOptionPane.showMessageDialog(this, "Add course " + selected + " successful!");
        } else {
            JOptionPane.showMessageDialog(this, "Already add course or Course not ready");
        }
    }

    private void startSelectedLesson(String category) {
        selectedCategory = category;
        service.startLesson(category);
        dashboardLayout.show(dashboardCards, "quiz");
        loadQuestion();
    }

    // ================= LESSON QUIZ =================

    private JPanel createQuizPanel() {

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);

        JButton backButton = new JButton("← Back");
        styleDarkTextButton(backButton, UITheme.PALE_GREEN);
        backButton.addActionListener(e -> dashboardLayout.show(dashboardCards, "lessons"));

        JPanel quizCard = UITheme.card();
        quizCard.setLayout(new BoxLayout(quizCard, BoxLayout.Y_AXIS));

        JLabel title = UITheme.heading("Practice", 23);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        quizWord = new JLabel("Select lesson", SwingConstants.CENTER);
        quizWord.setAlignmentX(Component.CENTER_ALIGNMENT);
        quizWord.setFont(new Font("Tahoma", Font.BOLD, 25));
        quizWord.setForeground(UITheme.INK);

        quizFeedback = new JLabel("pick the correct answer", SwingConstants.CENTER);
        quizFeedback.setAlignmentX(Component.CENTER_ALIGNMENT);
        quizFeedback.setFont(new Font("Tahoma", Font.BOLD, 15));
        quizFeedback.setForeground(UITheme.MUTED);

        choicePanel = new JPanel(new GridLayout(0, 2, 12, 12));
        choicePanel.setOpaque(false);
        choicePanel.setMaximumSize(new Dimension(650, 220));

        quizCard.add(title);
        quizCard.add(Box.createVerticalStrut(30));
        quizCard.add(quizWord);
        quizCard.add(Box.createVerticalStrut(26));
        quizCard.add(choicePanel);
        quizCard.add(Box.createVerticalStrut(18));
        quizCard.add(quizFeedback);

        content.add(backButton, BorderLayout.NORTH);
        content.add(quizCard, BorderLayout.CENTER);
        return content;
    }

    // ================= LOAD QUESTION =================

    private void loadQuestion() {

        Word word = service.nextQuestion();
        questionScored = false;
        choicePanel.removeAll();

        if (word == null) {
            quizWord.setText("End Lesson!");
            quizFeedback.setForeground(UITheme.DARK_GREEN);
            quizFeedback.setText("Bravo! score " + data.getXp() + " XP");

            JButton backButton = new JButton("Back");
            styleDarkTextButton(backButton, UITheme.GREEN);
            backButton.addActionListener(e -> dashboardLayout.show(dashboardCards, "lessons"));
            choicePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            choicePanel.add(backButton);

            refreshStats();
            DataStore.save(data);
            choicePanel.revalidate();
            choicePanel.repaint();
            return;
        }

        String questionText = "What is “" + word.getEnglish() + "” mean?";
        quizWord.setText(questionText);
        quizWord.setFont(fontForLanguageText(questionText, Font.BOLD, 25));
        quizFeedback.setForeground(UITheme.MUTED);
        quizFeedback.setText("pick the answer");

        List<Word> candidates = new ArrayList<>(service.getWordsInCategory(selectedCategory));
        if (candidates.size() < 4) {
            candidates = service.getAllWordsSorted();
        }
        candidates.removeIf(candidate -> candidate.getThai().equals(word.getThai()));
        Collections.shuffle(candidates);

        List<String> answers = new ArrayList<>();
        answers.add(word.getThai());
        for (Word candidate : candidates) {
            if (answers.size() == 4) break;
            if (!answers.contains(candidate.getThai())) answers.add(candidate.getThai());
        }
        Collections.shuffle(answers);

        choicePanel.setLayout(new GridLayout(0, 2, 12, 12));
        for (String answer : answers) {
            JButton option = new JButton(answer);
            styleDarkTextButton(option, UITheme.PALE_GREEN);
            option.setFont(new Font("Tahoma", Font.BOLD, 17));
            option.setPreferredSize(new Dimension(240, 58));
            option.addActionListener(e -> checkChoice(answer));
            choicePanel.add(option);
        }

        choicePanel.revalidate();
        choicePanel.repaint();
    }

    // ================= CHECK CHOICE =================

    private void checkChoice(String answer) {

        Word current = service.getCurrentWord();
        if (current == null) return;

        // หากตอบถูกไปแล้วแต่เลือกพักไว้ ให้กดคำตอบเดิมอีกครั้งเพื่อเปิดคำถามยืนยันใหม่
        if (questionScored) {
            if (current.getThai().equals(answer)) askToContinue();
            return;
        }

        boolean correct = service.checkAnswer(answer);

        if (correct) {
            questionScored = true;
            quizFeedback.setForeground(UITheme.DARK_GREEN);
            quizFeedback.setText("Correct! +10 XP");
            refreshStats();
            DataStore.save(data);
            askToContinue();
        } else {
            quizFeedback.setForeground(new Color(210, 70, 70));
            quizFeedback.setText("Incorrect try again. Answer: " + current.getThai());
            refreshStats();
            DataStore.save(data);
        }
    }

    private void askToContinue() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Correct! continue to the next quiz?",
                "continue to the next quiz?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            loadQuestion();
        } else {
            quizFeedback.setForeground(UITheme.BLUE);
            quizFeedback.setText("Take a break. continue press the correct answer.");
        }
    }

    // ================= STAT CARD =================

    private JLabel statCard(
            String title,
            String value,
            Color color
    ) {

        JLabel label = new JLabel(value);

        label.setFont(
                new Font("Tahoma", Font.BOLD, 25)
        );

        label.setForeground(color);

        return label;
    }

    private JPanel wrapStat(
            String title,
            JLabel value,
            Color color
    ) {

        JPanel card = UITheme.card();

        card.setLayout(
                new BoxLayout(card, BoxLayout.Y_AXIS)
        );

        JLabel name = new JLabel(title);

        name.setFont(
                new Font("Tahoma", Font.PLAIN, 14)
        );

        name.setForeground(UITheme.MUTED);

        card.add(name);
        card.add(Box.createVerticalStrut(10));
        card.add(value);

        return card;
    }

    // ================= VOCABULARY =================

    private JPanel createVocabulary() {

        JPanel panel = page();

        JLabel title = UITheme.heading(
                "Vocabulary", 23
        );

        String[] columns = {
                "Words",
                "Translate",
                "Categories",
                "Pronounce"
        };

        DefaultTableModel model =
                new DefaultTableModel(columns, 0) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        for (Word word : service.getAllWordsSorted()) {

            model.addRow(new Object[]{

                    word.getEnglish(),
                    word.getThai(),
                    word.getCategory(),
                    word.getPronounce()

            });

        }

        JTable table = new JTable(model);

        table.setRowHeight(30);

        table.setFont(
                new Font("Tahoma", Font.PLAIN, 13)
        );
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable source, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(
                        source, value, isSelected, hasFocus, row, column);
                component.setFont(fontForLanguageText(
                        value == null ? "" : value.toString(), Font.PLAIN, 13));
                return component;
            }
        });

        table.getTableHeader().setFont(
                new Font("Tahoma", Font.BOLD, 13)
        );

        table.setSelectionBackground(
                UITheme.GREEN
        );

        JTextField search = new JTextField();

        search.setFont(
                new Font("Tahoma", Font.PLAIN, 15)
        );

        search.setToolTipText(
                "find word"
        );

        JButton find = new JButton("Search");

        styleDarkTextButton(
                find,
                UITheme.GREEN
        );

        find.addActionListener(e -> {

            Word word = service.findWord(
                    search.getText()
            );

            if (word == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Word not found"
                );

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        word.getEnglish() +
                                " = " +
                                word.getThai() +
                                "\nPronounce: " +
                                word.getPronounce()
                );
            }
        });

        JPanel searchPanel = new JPanel(
                new BorderLayout(8, 8)
        );

        searchPanel.setOpaque(false);

        searchPanel.add(
                search,
                BorderLayout.CENTER
        );

        searchPanel.add(
                find,
                BorderLayout.EAST
        );

        panel.add(title, BorderLayout.NORTH);

        panel.add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        panel.add(
                searchPanel,
                BorderLayout.SOUTH
        );

        return panel;
    }

    // ================= REVIEW =================

    private JPanel createReview() {

        JPanel panel = page();

        JLabel title = UITheme.heading(
                "Review vocabularies", 23
        );

        DefaultListModel<String> model =
                new DefaultListModel<>();

        JList<String> list = new JList<>(model);

        list.setFont(
                new Font("Tahoma", Font.PLAIN, 16)
        );
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> source, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        source, value, index, isSelected, cellHasFocus);
                label.setFont(fontForLanguageText(
                        value == null ? "" : value.toString(), Font.PLAIN, 16));
                return label;
            }
        });

        list.setFixedCellHeight(34);

        JButton refresh = new JButton("Refresh");

        JButton done = new JButton(
                "Mark down"
        );

        styleDarkTextButton(
                refresh,
                UITheme.BLUE
        );

        styleDarkTextButton(
                done,
                UITheme.GREEN
        );

        refresh.addActionListener(e -> {

            model.clear();

            for (Word word : service.getReviewWords()) {

                model.addElement(
                        word.getEnglish() +
                                " — " +
                                word.getThai()
                );
            }
        });

        done.addActionListener(e -> {

            String selected = list.getSelectedValue();

            if (selected == null) return;

            String english =
                    selected.split(" — ")[0];

            service.removeFromReview(english);

            model.removeElement(selected);

            DataStore.save(data);
        });

        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        actions.setOpaque(false);

        actions.add(refresh);
        actions.add(done);

        panel.add(title, BorderLayout.NORTH);

        panel.add(
                new JScrollPane(list),
                BorderLayout.CENTER
        );

        panel.add(
                actions,
                BorderLayout.SOUTH
        );

        return panel;
    }

    // ================= HISTORY =================

    private JPanel createHistory() {

        JPanel panel = page();

        JLabel title = UITheme.heading(
                "History", 23
        );

        DefaultListModel<String> model =
                new DefaultListModel<>();

        for (String item : data.getActivityLog()) {
            model.addElement(item);
        }

        JList<String> list = new JList<>(model);

        list.setFont(
                new Font("Tahoma", Font.PLAIN, 14)
        );

        list.setFixedCellHeight(32);

        JButton refresh = new JButton(
                "Refresh"
        );

        styleDarkTextButton(
                refresh,
                UITheme.BLUE
        );

        refresh.addActionListener(e -> {

            model.clear();

            for (String item : data.getActivityLog()) {
                model.addElement(item);
            }
        });

        panel.add(title, BorderLayout.NORTH);

        panel.add(
                new JScrollPane(list),
                BorderLayout.CENTER
        );

        panel.add(
                refresh,
                BorderLayout.SOUTH
        );

        return panel;
    }

    // ================= ACCURACY =================

    private int accuracy() {

        if (data.getTotalAnswers() == 0) {
            return 0;
        }

        return (int) Math.round(
                data.getCorrectAnswers() *
                        100.0 /
                        data.getTotalAnswers()
        );
    }

    // ================= REFRESH STATS =================

    private void refreshStats() {

        if (xpValue != null) {
            xpValue.setText(data.getXp() + " XP");
        }

        if (accuracyValue != null) {
            accuracyValue.setText(accuracy() + "%");
        }

        if (lessonValue != null) {

            lessonValue.setText(
                    String.valueOf(
                            data.getLessonsCompleted()
                    )
            );
        }
    }
}