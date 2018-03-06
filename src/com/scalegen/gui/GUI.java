package com.scalegen.gui;

import com.scalegen.defs.*;
import com.scalegen.generation.ArpeggioGenerator;
import com.scalegen.generation.GenerationResult;
import com.scalegen.generation.Generator;
import com.scalegen.generation.ScaleGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

class GUI extends JFrame{

    private static final String TITLE = "ScaleMaster 0.1";
    private static final String MSG_INCOMPLETE = "The end of the fretboard was reached before the generation was completed. Try placing the root in on lower string.";

    private final JPanel contentPane;
    private final JPanel optionsPane;

    private final JPanel optionsTop;
    private final JPanel optionsBottom;

    private JComboBox<GenerationType> type;
    private JComboBox<Note> root;
    private JComboBox<Mode> mode;
    private JComboBox<GenerationLength> length;
    private JComboBox<Location> rootPosition;
    private FretboardCanvas canvas;
    private JButton generate;
    private JLabel resultLabel;

    private final Generator scaleGenerator, arpeggioGenerator;

    GUI(){
        setTitle(TITLE);
        setResizable(false);

        scaleGenerator = new ScaleGenerator();
        arpeggioGenerator = new ArpeggioGenerator();

        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        optionsPane = new JPanel();
        optionsPane.setLayout(new BorderLayout());

        optionsPane.add(optionsTop = new JPanel(),BorderLayout.NORTH);
        optionsPane.add(optionsBottom = new JPanel(),BorderLayout.SOUTH);

        topOptions();
        bottomOptions();

        try {
            contentPane.add(canvas = new FretboardCanvas(this), BorderLayout.CENTER);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,"A required resource file was not found.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            e.printStackTrace();
        }

        /*resultLabel = new JLabel("Click on generate");
        resultLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(resultLabel, BorderLayout.SOUTH);*/

        contentPane.add(optionsPane, BorderLayout.NORTH);

    }

    private void topOptions() {

        /*Type selector, either scale or arpeggio*/
        optionsTop.add(new JLabel("Type:"));
        type = new JComboBox<>(GenerationType.values());
        type.setSelectedItem(GenerationType.SCALE);
        type.addActionListener(this::typeChanged);
        optionsTop.add(type);

        /*Length, single-octave or two-octave*/
        optionsTop.add(new JLabel("Length:"));
        length = new JComboBox<>(GenerationLength.values());
        length.setSelectedItem(GenerationLength.TWO_OCTAVES);
        optionsTop.add(length);

        /*Root selection*/
        optionsTop.add(new JLabel("Root:"));
        root = new JComboBox<>(Note.values());
        root.setSelectedItem(Note.C);
        root.addActionListener(this::rootChanged);
        optionsTop.add(root);

        /*Mode: major, minor, diminished, etc...*/
        optionsTop.add(new JLabel("Mode:"));
        mode = new JComboBox<>(Mode.FOR_SCALES);
        mode.setSelectedItem(Mode.MAJOR);
        optionsTop.add(mode);
    }

    private void bottomOptions() {
        /*Root starting position...*/
        optionsBottom.add(new JLabel("Starting at:"));
        rootPosition = new JComboBox<>(Location.getAll(Note.C));
        optionsBottom.add(rootPosition);

        generate = new JButton("Generate");
        generate.addActionListener(this::generate);
        optionsBottom.add(generate);
    }

    private void rootChanged(ActionEvent e) {
        rootPosition.setModel(new DefaultComboBoxModel<>(Location.getAll((Note) root.getSelectedItem())));
    }

    private void typeChanged(ActionEvent e) {
        Mode[] modes;
        if(Objects.equals(type.getSelectedItem(), GenerationType.SCALE))
            modes = Mode.FOR_SCALES;
        else
            modes = Mode.FOR_ARPEGGIOS;
        mode.setModel(new DefaultComboBoxModel<>(modes));
    }
    private void generate(ActionEvent e) {
        GenerationResult result;
        if(GenerationType.SCALE.equals(type.getSelectedItem()))
            result = scaleGenerator.generate((GenerationLength)length.getSelectedItem(),(Note) root.getSelectedItem(), (Mode) mode.getSelectedItem(), (Location) rootPosition.getSelectedItem());
        else
            result = arpeggioGenerator.generate((GenerationLength)length.getSelectedItem(),(Note) root.getSelectedItem(), (Mode) mode.getSelectedItem(), (Location) rootPosition.getSelectedItem());
        canvas.updateNotes(result.getLocations(), (Note) root.getSelectedItem());
        if(result.isIncomplete())
            JOptionPane.showMessageDialog(this, MSG_INCOMPLETE, "Scale is incomplete", JOptionPane.WARNING_MESSAGE);
    }

    void noteClicked(Location location) {
        root.setSelectedItem(location.note);
        rootPosition.setSelectedItem(location);
        generate(null);
    }
}
