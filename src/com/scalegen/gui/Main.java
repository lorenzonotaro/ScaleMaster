package com.scalegen.gui;

import com.scalegen.defs.Note;

import javax.swing.*;
import java.awt.*;

public class Main {
    private static final int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 300;
    public static void main(String[] args){
        for(Note t : Note.naturalMinorScale(Note.C))
            System.out.print(t + " ");
        EventQueue.invokeLater(() -> {
            GUI f = new GUI();
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            f.setVisible(true);
        });
    }
}
