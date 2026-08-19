package editor;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class TextEditor extends JFrame {
    public TextEditor() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setLocationRelativeTo(null);
        setLayout(null);
        setTitle("The first stage");
        setBackground(new Color(239, 239, 239));
        JTextArea textArea = new JTextArea();
        textArea.setBounds(20, 20, 240, 240);
        textArea.setBackground(Color.white);
        add(textArea);
        setVisible(true);
    }

    public static void runFrame() throws InterruptedException, InvocationTargetException {
        Runnable r = TextEditor::new;
        SwingUtilities.invokeAndWait(r);
    }
}
