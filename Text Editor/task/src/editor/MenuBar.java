package editor;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class MenuBar extends JMenuBar {

    public static final String FILE_MENU_LOAD = "Open";
    public static final String FILE_MENU_EXIT = "Exit";
    public static final String FILE_MENU_SAVE = "Save";

    public static final String SEARCH_MENU_START = "Start search";
    public static final String SEARCH_MENU_PREVIOUS = "Previous search";
    public static final String SEARCH_MENU_NEXT = "Next match";
    public static final String SEARCH_MENU_REGEX = "Use regular expressions";

    private JMenu fileMenu;
    private JMenuItem searchMenu;
    private Map<String, JMenuItem> fileMenuItems = new HashMap<>();
    private Map<String, JMenuItem> searchMenuItems = new HashMap<>();

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

        loadMenuItem.setName("MenuOpen");
        saveMenuItem.setName("MenuSave");
        exitMenuItem.setName("MenuExit");

        fileMenuItems.put(FILE_MENU_LOAD, loadMenuItem);
        fileMenuItems.put(FILE_MENU_EXIT, exitMenuItem);
        fileMenuItems.put(FILE_MENU_SAVE, saveMenuItem);

        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        searchMenu = new JMenu("Search");
        searchMenu.setName("MenuSearch");
        searchMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem startSearchMenuItem = new JMenuItem(SEARCH_MENU_START);
        JMenuItem prevSearchMenuItem = new JMenuItem(SEARCH_MENU_PREVIOUS);
        JMenuItem nextSearchMenuItem = new JMenuItem(SEARCH_MENU_NEXT);
        JMenuItem regexSearchMenuItem = new JMenuItem(SEARCH_MENU_REGEX);

        startSearchMenuItem.setName("MenuStartSearch");
        prevSearchMenuItem.setName("MenuPreviousMatch");
        nextSearchMenuItem.setName("MenuNextMatch");
        regexSearchMenuItem.setName("MenuUseRegExp");

        searchMenuItems.put(SEARCH_MENU_START, startSearchMenuItem);
        searchMenuItems.put(SEARCH_MENU_PREVIOUS, prevSearchMenuItem);
        searchMenuItems.put(SEARCH_MENU_NEXT, nextSearchMenuItem);
        searchMenuItems.put(SEARCH_MENU_REGEX, regexSearchMenuItem);

        searchMenu.add(startSearchMenuItem);
        searchMenu.add(prevSearchMenuItem);
        searchMenu.add(nextSearchMenuItem);
        searchMenu.add(regexSearchMenuItem);

        this.add(fileMenu);
        this.add(searchMenu);
    }

    public void setActionToOption(String option, ActionListener actionListener, MenuType type) {
        Map<String, JMenuItem> menuItems = switch (type) {
            case FILE ->  fileMenuItems;
            case SEARCH -> searchMenuItems;
        };
        JMenuItem item = menuItems.get(option);
        if (item != null) {
            item.addActionListener(actionListener);
        }
    }

    public enum MenuType {
        FILE,
        SEARCH,
    }
}
