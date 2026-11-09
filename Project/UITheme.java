package Project;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicButtonUI;

public class UITheme {

    // สีหลักของโปรแกรม
    public static final Color GREEN = new Color(88, 204, 2);
    public static final Color DARK_GREEN = new Color(70, 170, 0);
    public static final Color PALE_GREEN = new Color(238, 255, 218);
    public static final Color CREAM = new Color(250, 250, 245);
    public static final Color INK = new Color(55, 70, 79);
    public static final Color MUTED = new Color(119, 135, 145);
    public static final Color BORDER = new Color(225, 231, 235);
    public static final Color BLUE = new Color(28, 176, 246);
    public static final Color ORANGE = new Color(255, 150, 0);

    // สีตัวอักษรของปุ่ม
    public static final Color BUTTON_TEXT = new Color(40, 55, 65);

    private UITheme() {}

    // รูปแบบปุ่มมาตรฐาน
    public static void styleButton(JButton button, Color color) {

        button.setUI(new BasicButtonUI());

        button.setFont(new Font("Tahoma", Font.BOLD, 14));

        button.setForeground(BUTTON_TEXT);

        button.setBackground(color);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);

        button.setEnabled(true);
        button.setFocusPainted(false);

        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        button.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 4, 0, color.darker()),
                new EmptyBorder(10, 18, 8, 18)
        ));
    }

    public static JPanel card() {
        JPanel panel = new JPanel();

        panel.setBackground(Color.WHITE);

        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        return panel;
    }

    public static JLabel heading(String text, int size) {

        JLabel label = new JLabel(text);

        label.setFont(new Font("Tahoma", Font.BOLD, size));
        label.setForeground(INK);

        return label;
    }
}