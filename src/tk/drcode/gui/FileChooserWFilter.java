package tk.drcode.gui;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FileChooserUI;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.lang.reflect.Field;

/**
 * Modified class of JFileChooser
 * Added ability to filter list of files by typing a filename at the file name text fileld.
 * The filtering will work only if you create the FileFilter by makeFileFilter method.
 */
public class FileChooserWFilter extends JFileChooser {

    public FileChooserWFilter() {
        super();

        FileChooserUI fcUI = searchFileChooserUI(this);
        if (fcUI == null)
            return;
        Field mfield = null;
        try {
            mfield = fcUI.getClass().getDeclaredField("fileNameTextField");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        if (mfield == null) {
            try {
                mfield = fcUI.getClass().getDeclaredField("filenameTextField");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            mfield.setAccessible(true);
            final JTextField tfFileName = (JTextField) mfield.get(fcUI);
            tfFileName.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    FileFilter ff = getFileFilter();
                    if (ff != null && ff instanceof FileFilterFJWF)
                        FileChooserWFilter.this.setFileFilter(makeFileFilter(((FileFilterFJWF) ff).getExtension(), ff.getDescription(), tfFileName.getText()));
                }
            });
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    static private FileChooserUI searchFileChooserUI(Container panel) {
        for (java.awt.Component c : panel.getComponents()) {

            if (c instanceof JPanel) {
                Object fcUI = searchFileChooserUI((JPanel) c);
                if (fcUI != null)
                    return (FileChooserUI) fcUI;
            } else if ((c.getClass().getName().startsWith("javax.swing.plaf.metal.MetalFileChooserUI$")
                    || c.getClass().getName().startsWith("com.sun.java.swing.plaf.windows.WindowsFileChooserUI$"))
                    && c.getClass().getDeclaringClass() == null) {
                for (Field field : c.getClass().getDeclaredFields()) {
                    if (field.getType().getName().equals("com.sun.java.swing.plaf.windows.WindowsFileChooserUI")
                            || field.getType().getName().equals("javax.swing.plaf.metal.MetalFileChooserUI")) {
                        field.setAccessible(true);
                        try {
                            return (FileChooserUI) field.get(c);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Make a file filter
     *
     * @param extension   file extension filter
     * @param description file extension description
     * @param startWith   file name "start with" filter
     * @return FileFilter
     */
    public static FileFilter makeFileFilter(String extension, String description, String startWith) {
        return new FileFilterFJWF(extension, description, startWith);
    }

    /**
     * Make a file filter
     *
     * @param extension   file extension filter
     * @param description file extension description
     * @return FileFilter
     */
    public static FileFilter makeFileFilter(String extension, String description) {
        return makeFileFilter(extension, description, "");
    }

    static protected class FileFilterFJWF extends FileFilter {
        private final String extension;
        private final String description;
        private String startWith;

        public FileFilterFJWF(String extension, String description, String startWith) {
            super();
            this.extension = extension.toLowerCase();
            this.description = description;
            this.startWith = startWith.toLowerCase();
        }

        @Override
        public boolean accept(File f) {
            if (f.isDirectory())
                return true;
            return !(startWith != null && !startWith.isEmpty() && !f.getName().toLowerCase().startsWith(startWith)) && f.getName().toLowerCase().endsWith(extension);
        }

        @Override
        public String getDescription() {
            return description;
        }

        public String getExtension() {
            return extension;
        }

        public String getStartWith() {
            return startWith;
        }
    }
}