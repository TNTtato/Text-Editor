package editor;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

public class TextEditor extends JFrame {

    private JTextArea textArea;
    private JButton saveButton;
    private JButton loadButton;
    private JTextField filenameField;
    private JScrollPane scrollPane;

    public TextEditor() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));
        setTitle("Text Editor");
        setBackground(new Color(239, 239, 239));

        textArea = new JTextArea();
        textArea.setBounds(20, 20, 240, 240);
        textArea.setBackground(Color.white);
        textArea.setName("TextArea");
        scrollPane = new JScrollPane(textArea);
        scrollPane.setName("ScrollPane");
        JPanel left = new JPanel();
        JPanel right = new JPanel();
        JPanel bottom = new JPanel();
        scrollPane.setPreferredSize(new Dimension(240, 240));
        add(loadSavePanel(), BorderLayout.NORTH);
        add(left,  BorderLayout.WEST);
        add(right, BorderLayout.EAST);
        add(scrollPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        setVisible(true);
    }

    public static void runFrame() throws InterruptedException, InvocationTargetException {
        Runnable r = TextEditor::new;
        SwingUtilities.invokeAndWait(r);
    }

    private JPanel loadSavePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        filenameField = new JTextField();
        filenameField.setName("FilenameField");
        filenameField.setPreferredSize(new Dimension(200, 20));
        filenameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(filenameField);
        Dimension btnDim = new Dimension(100, 20);
        saveButton = new JButton("Save");
        saveButton.setName("SaveButton");
        saveButton.addActionListener( e -> {
            if (filenameField == null || filenameField.getText().trim().equals("")) {
                return;
            }

            File file = new File(filenameField.getText().trim());
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                    throw new RuntimeException(ex);
                }
            }

            try(FileWriter fw = new FileWriter(file)) {
                fw.append(textArea.getText());
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                throw new RuntimeException(ex);
            }

        });
        saveButton.setPreferredSize(btnDim);
        loadButton = new JButton("Load");
        loadButton.setName("LoadButton");
        loadButton.setPreferredSize(btnDim);
        loadButton.addActionListener( e -> {
           if (filenameField == null || filenameField.getText().trim().equals("")) {
               return;
           }

           File file = new File(filenameField.getText().trim());
           if (!file.exists()) {
               textArea.setText("");
               return;
           }

            try(FileReader fr = new FileReader(file)) {
                StringBuilder sb = new StringBuilder();
                int c;
                while ((c = fr.read()) != -1)
                    sb.append((char) c);

                textArea.setText(sb.toString());
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                throw new RuntimeException(ex);
            }

        });
        panel.add(saveButton);
        panel.add(loadButton);
        return panel;
    }
}
