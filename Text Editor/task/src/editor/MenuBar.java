package editor;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class MenuBar extends JMenuBar {

    public static final String FILE_MENU_LOAD = "Load";
    public static final String FILE_MENU_EXIT = "Exit";
    public static final String FILE_MENU_SAVE = "Save";

    private JMenu fileMenu;
    private Map<String, JMenuItem> fileMenuItems = new HashMap<>();

    public MenuBar() {
        super();
        initFileMenu();
    }

    public JMenu getFileMenu() {
        return fileMenu;
    }

    private void initFileMenu() {
        fileMenu = new JMenu("File");
        fileMenu.setName("MenuFile");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem loadMenuItem = new JMenuItem(FILE_MENU_LOAD);
        JMenuItem saveMenuItem = new JMenuItem(FILE_MENU_SAVE);
        JMenuItem exitMenuItem = new JMenuItem(FILE_MENU_EXIT);

        loadMenuItem.setName("MenuLoad");
        saveMenuItem.setName("MenuSave");
        exitMenuItem.setName("MenuExit");

        fileMenuItems.put(FILE_MENU_LOAD, loadMenuItem);
        fileMenuItems.put(FILE_MENU_EXIT, exitMenuItem);
        fileMenuItems.put(FILE_MENU_SAVE, saveMenuItem);

        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        this.add(fileMenu);
    }

    public void setActionToOption(String option, ActionListener actionListener) {
        JMenuItem item = fileMenuItems.get(option);
        if (item != null) {
            item.addActionListener(actionListener);
        }
    }
}
