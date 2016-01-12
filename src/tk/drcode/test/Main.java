package tk.drcode.test;

import tk.drcode.gui.FileChooserWFilter;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFileChooser chooser = new FileChooserWFilter();
        chooser.setFileFilter(FileChooserWFilter.makeFileFilter(".png", "*.png"));
        chooser.showOpenDialog(new JFrame("FileDialog GUI"));
    }
}
