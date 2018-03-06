package com.scalegen.gui;

import com.scalegen.defs.Location;
import com.scalegen.defs.Note;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.io.InputStream;

public class FretboardCanvas extends JComponent{

    private static final String FRETBOARD_PNG_LOCATION = "com/scalegen/res/fretboard.png";

    private static final int FIRST_STRING_Y = 38;
    private static final int DY_BETWEEN_STRINGS = 23;

    private static final int[] FRET_X_VALUES =
            {100, 160, 215, 267, 317, 367, 413, 458, 504, 543, 583, 617, 642, 665, 686, 700};

    /*private static final int[] FRET_X_VALUES =
            { 100, 159, 215, 267, 317, 367, 414, 459, 503, 521, 544, 582, 618, 643, 665, 685, 700};
            //open  1    2    3    4    5    6    7    8    9    10   11   12   13   14   15   15 */
    private static final int CIRCLE_RADIUS = 15;

    private static final Color NOTE_COLOR  = new Color(255, 70, 70);
    private static final Color ROOT_COLOR  = new Color(70,70,255);
    private static final Color HOVER_COLOR = new Color(170,170,170,100);
    private static final Color TEXT_COLOR  = Color.BLACK;

    private final Image fretboard;
    private final GUI mainFrame;
    private Note root;
    private Location[] notes;
    private Point fretboardCoords;
    private Location hoveredNote;


    FretboardCanvas(GUI mainFrame) throws IOException {
        InputStream stream = FretboardCanvas.class.getClassLoader().getResourceAsStream(FRETBOARD_PNG_LOCATION);
        if(stream == null) throw new IOException();
        fretboard = ImageIO.read(stream);
        this.fretboardCoords = new Point(this.getWidth()/2 - (fretboard.getWidth(null) / 2),getHeight()/2 - (fretboard.getHeight(null) / 2));
        root = Note.C;
        notes = new Location[]{};
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.printf("x: %d, y: %d\n",e.getX(),e.getY());
                if(hoveredNote != null) mainFrame.noteClicked(hoveredNote);
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                int x  = e.getX(), y = e.getY();
                if(new Rectangle(fretboardCoords.x, fretboardCoords.y, fretboard.getWidth(null), fretboard.getHeight(null)).contains(x,y)){
                    int string, fret;

                    if(FretboardCanvas.this.hoveredNote != null){
                        string = hoveredNote.string;
                        fret = hoveredNote.fret;
                    }else{
                        string = 1;
                        fret = 0;
                    }
                    for(int i = FIRST_STRING_Y, k = 1; i <= FIRST_STRING_Y + DY_BETWEEN_STRINGS * 5; i += DY_BETWEEN_STRINGS, k++) {
                        if (y > i - DY_BETWEEN_STRINGS / 2 && y < i + DY_BETWEEN_STRINGS / 2) {
                            string = k;
                            break;
                        }
                    }

                    if(!(x > FRET_X_VALUES[0] - 5 && x < FRET_X_VALUES[1] + 5))
                        for(int i = 1; i < FRET_X_VALUES.length; i++){
                            if(x >= FRET_X_VALUES[i - 1] && x <= FRET_X_VALUES[i]){
                                fret = i;
                                break;
                            }
                        }
                    FretboardCanvas.this.hoveredNote = new Location(string,fret);
                    repaint();
                }
            }
        });
        this.mainFrame = mainFrame;
    }

    @Override
    public void paintComponent(Graphics g){
        this.fretboardCoords = new Point(this.getWidth()/2 - (fretboard.getWidth(null) / 2),getHeight()/2 - (fretboard.getHeight(null) / 2));
        g.drawImage(fretboard,fretboardCoords.x,fretboardCoords.y,null);
        drawText(g);
        if(this.hoveredNote != null)
            drawNote(g, hoveredNote, HOVER_COLOR);
        for(Location note : notes){
            if(note == null) continue;
            drawNote(g, note, note.note.equals(root) ? ROOT_COLOR : NOTE_COLOR);
        }
    }

    private void drawText(Graphics g) {
        int offsetx = this.fretboardCoords.x + fretboard.getWidth(null) + 30;
        int offsety = this.fretboardCoords.y + 30;
        g.drawString("Root note ", offsetx, offsety);
        g.setColor(ROOT_COLOR);
        g.fillOval(offsetx - CIRCLE_RADIUS - 5, offsety - CIRCLE_RADIUS / 2 - 6, CIRCLE_RADIUS, CIRCLE_RADIUS);
    }

    private void drawNote(Graphics g, Location note, Color color) {
        g.setColor(color);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
        int xcenter = note.fret == 0 ? FRET_X_VALUES[0] : (FRET_X_VALUES[note.fret] + FRET_X_VALUES[note.fret - 1]) / 2;
        int x = xcenter - CIRCLE_RADIUS/2;
        int y = FIRST_STRING_Y + DY_BETWEEN_STRINGS * (note.string - 1) - CIRCLE_RADIUS/2;
        g.fillOval(x, y, CIRCLE_RADIUS, CIRCLE_RADIUS);
        drawCenteredString(g, note.note.toString() , x, y, CIRCLE_RADIUS, CIRCLE_RADIUS);
    }

    private void drawCenteredString(Graphics g, String text, int x, int y, int width, int height) {
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int sx = x + (width - metrics.stringWidth(text)) / 2;
        int sy = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.setColor(TEXT_COLOR);
        g.drawString(text, sx, sy);
    }
    void updateNotes(Location[] notes, Note root){
        this.notes = notes;
        this.root = root;
        repaint();
    }
}
