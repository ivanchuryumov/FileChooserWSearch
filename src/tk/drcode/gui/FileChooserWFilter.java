package tk.drcode.gui;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
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
        JPanel panel1 = (JPanel) this.getComponent(3);
        JPanel panel3 = (JPanel) panel1.getComponent(0);
        Field field = panel3.getComponent(1).getClass().getDeclaredFields()[0];
        field.setAccessible(true);
        try {
            javax.swing.plaf.metal.MetalFileChooserUI mfcUI = (javax.swing.plaf.metal.MetalFileChooserUI) field.get(panel3.getComponent(1));
            Field mfield = mfcUI.getClass().getDeclaredField("fileNameTextField");
            mfield.setAccessible(true);
            JTextField tfFileName = (JTextField) mfield.get(mfcUI);
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
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
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
