package editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame {

    private JTextArea textArea;
    private JButton saveButton;
    private JButton openButton;
    private JButton searchButton;
    private JButton prevSearchButton;
    private JButton nextSearchButton;
    private JCheckBox regExCheckBox;
    private JTextField searchField;
    private JScrollPane scrollPane;
    private MenuBar menuBar;
    private ActionListener saveListener = getSaveAction();
    private ActionListener loadListener = getLoadAction();
    private boolean useRegex = false;
    private List<FindResult> finds;
    private JFileChooser chooser;
    int position = 0;

    private String fileName;

    public TextEditor() {
        chooser = new JFileChooser();
        chooser.setName("FileChooser");
        add(chooser, BorderLayout.CENTER);
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

        menuBar = new MenuBar();
        setMenuBarActions();
        setJMenuBar(menuBar);

        setVisible(true);
    }

    public static void runFrame() throws InterruptedException, InvocationTargetException {
        Runnable r = TextEditor::new;
        SwingUtilities.invokeAndWait(r);
    }

    private JPanel loadSavePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        ImageIcon openIcon = new ImageIcon("open.png");
        ImageIcon saveIcon = new ImageIcon("save.png");
        ImageIcon searchIcon = new ImageIcon("search.png");
        ImageIcon leftIcon = new ImageIcon("left.png");
        ImageIcon rightIcon = new ImageIcon("right.png");


        searchField = new JTextField();
        searchField.setName("SearchField");
        searchField.setPreferredSize(new Dimension(200, 40));
        searchField.setAlignmentX(Component.LEFT_ALIGNMENT);
        Dimension btnDim = new Dimension(40, 40);
        saveButton = new JButton(saveIcon);
        saveButton.setName("SaveButton");
        saveButton.addActionListener(saveListener);
        saveButton.setPreferredSize(btnDim);
        openButton = new JButton(openIcon);
        openButton.setName("OpenButton");
        openButton.setPreferredSize(btnDim);
        openButton.addActionListener(loadListener);
        searchButton = new JButton(searchIcon);
        prevSearchButton = new JButton(leftIcon);
        nextSearchButton = new JButton(rightIcon);
        regExCheckBox = new JCheckBox("Use regex");
        regExCheckBox.setName("UseRegExCheckbox");
        regExCheckBox.addActionListener(e -> useRegex = regExCheckBox.isSelected());

        searchButton.setName("StartSearchButton");
        searchButton.setPreferredSize(btnDim);
        prevSearchButton.setName("PreviousMatchButton");
        nextSearchButton.setName("NextMatchButton");
        prevSearchButton.setPreferredSize(btnDim);
        nextSearchButton.setPreferredSize(btnDim);

        searchButton.addActionListener(getSearchAction());
        nextSearchButton.addActionListener(getNextAction());
        prevSearchButton.addActionListener(getPreviousAction());


        panel.add(openButton);
        panel.add(saveButton);
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(prevSearchButton);
        panel.add(nextSearchButton);
        panel.add(regExCheckBox);


        return panel;
    }

    private ActionListener getSaveAction() {
        return e -> {
            int selection = chooser.showOpenDialog(null);
            if (selection == JFileChooser.APPROVE_OPTION)
                fileName = chooser.getSelectedFile().getAbsolutePath();
            else return;

            File file = new File(fileName);
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

        };
    }

    private ActionListener getLoadAction() {

        return e -> {
            int selection = chooser.showOpenDialog(null);
            if (selection != JFileChooser.APPROVE_OPTION) {
                return;
            }

            fileName = chooser.getSelectedFile().getAbsolutePath();
            File file = new File(fileName);
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

        };
    }

    private ActionListener getSearchAction() {

        return e -> {
            position = 0;

            String toFind = searchField.getText();

            finds = new ArrayList<>();

            SwingWorker<String, FindResult> worker = new SwingWorker<String, FindResult>() {
                @Override
                protected String doInBackground() throws Exception {
                    String target = useRegex ? toFind : Pattern.quote(toFind);
                    Pattern pattern = Pattern.compile(target);
                    Matcher matcher = pattern.matcher(textArea.getText());
                    System.out.println("To Find: " + toFind);

                    while (matcher.find()) {
                        System.out.println(matcher.group() + " " + matcher.start() + " " + matcher.end());
                        publish(new FindResult(matcher.group(), matcher.start()));
                    }
                    return "done";
                }

                protected void process(List<FindResult> chunks) {
                    for (FindResult result : chunks) {
                        finds.add(result);
                    }
                }

                @Override
                protected void done() {
                    if (finds != null && !finds.isEmpty()) {
                        highlightArea(finds.get(0));
                    }
                }
            };
            worker.execute();
        };
    }

    private ActionListener getPreviousAction() {
        return e -> {
            FindResult find = getPreviousResult();
            highlightArea(find);
        };
    }

    private ActionListener getNextAction() {
        return e -> {
            FindResult find = getNextResult();
            highlightArea(find);
        };
    }

    private void highlightArea(FindResult find) {
        if (find != null) {
            textArea.setCaretPosition(find.getIndex() + find.getExcerpt().length());
            textArea.select(find.getIndex(), find.getIndex() + find.getExcerpt().length());
            textArea.grabFocus();
        }
    }

    private void setMenuBarActions() {
        menuBar.setActionToOption(MenuBar.FILE_MENU_LOAD, loadListener, MenuBar.MenuType.FILE);
        menuBar.setActionToOption(MenuBar.FILE_MENU_SAVE, saveListener, MenuBar.MenuType.FILE);
        menuBar.setActionToOption(MenuBar.FILE_MENU_EXIT, e -> {
            this.dispose();
            System.exit(0);
        }, MenuBar.MenuType.FILE);

        menuBar.setActionToOption(MenuBar.SEARCH_MENU_START, getSearchAction(), MenuBar.MenuType.SEARCH);
        menuBar.setActionToOption(MenuBar.SEARCH_MENU_PREVIOUS, getPreviousAction(), MenuBar.MenuType.SEARCH);
        menuBar.setActionToOption(MenuBar.SEARCH_MENU_NEXT, getNextAction(), MenuBar.MenuType.SEARCH);
        menuBar.setActionToOption(MenuBar.SEARCH_MENU_REGEX,
                e -> {
            regExCheckBox.setSelected(!regExCheckBox.isSelected());
            useRegex = regExCheckBox.isSelected();
                },
                MenuBar.MenuType.SEARCH);
    }

    private FindResult getNextResult() {
        if (finds == null || finds.isEmpty()) {
            return null;
        }


        position = position + 1 > finds.size() - 1 ? 0 : position + 1;
        FindResult current = finds.get(position);

        return current;
    }

    private FindResult getPreviousResult() {
        if (finds == null || finds.isEmpty()) {
            return null;
        }

        position = position - 1 < 0 ? finds.size() - 1 : position - 1;
        FindResult current = finds.get(position);

        return current;
    }

    public class FindResult {
        private String excerpt;
        private int index;
        public FindResult(String excerpt, int index) {
            this.excerpt = excerpt;
            this.index = index;
        }

        public String getExcerpt() {
            return excerpt;
        }

        public int getIndex() {
            return index;
        }
    }
}
