/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package one.dedic.consolelode;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextGraphicsWriter;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.screen.WrapBehaviour;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.IOException;

/**
 *
 * @author sdedic
 */
public class ConsoleLode {

    public static void main(String[] args) throws Exception {
        
        Terminal t = new DefaultTerminalFactory().createTerminal();
        Screen scn = new TerminalScreen(t);
        
        scn.startScreen();
        
            // Get TextGraphics for drawing
            TextGraphics tg = scn.newTextGraphics();
            for (int i = 1; i <= 10; i++) {
                tg.putString(0, i + 1, "" + i, SGR.BOLD);
            }
            for (int i = 1; i <= 10; i++) {
                tg.putString(i * 2 + 1 , 0, String.valueOf((char)('A' - 1 + i)), SGR.BOLD);
            }
            
            TextGraphics tg2 = tg.newTextGraphics(new TerminalPosition(2, 1), new TerminalSize(22, 12));
            
            tg2.setCharacter(0, 0, Symbols.SINGLE_LINE_TOP_LEFT_CORNER);
            tg2.drawLine(1, 0, 20, 0, Symbols.SINGLE_LINE_HORIZONTAL);
            tg2.setCharacter(21, 0, Symbols.SINGLE_LINE_TOP_RIGHT_CORNER);
            tg2.drawLine(0, 1, 0, 10, Symbols.SINGLE_LINE_VERTICAL);
            tg2.drawLine(21, 1, 21, 10, Symbols.SINGLE_LINE_VERTICAL);
            tg2.drawLine(1, 11, 20, 11, Symbols.SINGLE_LINE_HORIZONTAL);
            
            // Define rectangle position and size
            TerminalPosition topLeft = new TerminalPosition(5, 3);
            TerminalSize size = new TerminalSize(20, 10);

            // Draw the rectangle
            //tg.drawRectangle(topLeft, size, Symbols.BLOCK_SOLID); // '*' is the character used
            
            //scn.newTextGraphics().putString(0, 15, "Stiskni jakoukoliv klavesu...");
            scn.setCursorPosition(new TerminalPosition(0, 15));
        
            vyberVelikostiLodi(scn);
        scn.refresh();
        scn.readInput();
        
        scn.close();
        
    }
    
    static void vyberVelikostiLodi(Screen scn) throws IOException {
        TextGraphics tg = scn.newTextGraphics();
        TextGraphics g = tg.newTextGraphics(new TerminalPosition(25, 0), new TerminalSize(60, 10));
        TextGraphicsWriter writer = new TextGraphicsWriter(g);
        writer.setWrapBehaviour(WrapBehaviour.WORD);
        writer.putString("Vyber velikost lodi k umisteni. V seznamu je uvedeny pro kazdou velikost lodi pocet zbyvajicich.\n");
        writer.putString("Pro vyber stiskni ");
        writer.enableModifiers(SGR.BOLD);
        writer.putString("cislo velikosti lode");
        writer.disableModifiers(SGR.BOLD);
        writer.putString(" a potvrd ");
        writer.enableModifiers(SGR.BOLD);
        writer.putString("ENTER");
        
        
        TextGraphics g2 = tg.newTextGraphics(new TerminalPosition(25, 4), new TerminalSize(60, 10));
        TextGraphicsWriter writer2 = new TextGraphicsWriter(g2);
        writer2.enableModifiers(SGR.BOLD).putString("Velikost   Zbyva");
        
        writer2.setCursorPosition(new TerminalPosition(4, 1));
        writer2.putString("1");
        writer2.setCursorPosition(new TerminalPosition(13, 1));
        writer2.putString("4");

        writer2.setCursorPosition(new TerminalPosition(4, 2));
        writer2.putString("2");
        writer2.setCursorPosition(new TerminalPosition(13, 2));
        writer2.putString("3");

        writer2.setCursorPosition(new TerminalPosition(4, 3));
        writer2.putString("3");
        writer2.setCursorPosition(new TerminalPosition(13, 3));
        writer2.putString("2");

        writer2.setCursorPosition(new TerminalPosition(4, 4));
        writer2.putString("4");
        writer2.setCursorPosition(new TerminalPosition(13, 4));
        writer2.putString("1");

        writer2.setCursorPosition(new TerminalPosition(4, 5));
        writer2.putString("4");
        writer2.setCursorPosition(new TerminalPosition(13, 5));
        writer2.putString("1");
        
        scn.setCursorPosition(new TerminalPosition(25 + 18, 5));
        scn.setCharacter(25 + 18, 5, new TextCharacter('?'));
        scn.refresh();
        
        int size = 1;
        L: while (true) {
            scn.setCharacter(25 + 18, 5 + size - 1, new TextCharacter(' '));
            KeyStroke ks = scn.readInput();
            
            int ns = -1;
            switch (ks.getKeyType()) {
                case ARROW_DOWN:
                    ns = Math.min(5, size + 1);
                    break;
                case ARROW_UP:
                    ns = Math.max(1, size - 1);
                    break;
                case ENTER:
                    break L;
                case CHARACTER:
                    switch (ks.getCharacter()) {
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                            ns = ks.getCharacter() - '0';
                            break;
                        case '\n':
                            break L;
                    }
            }
            if (ns != -1) {
                size = ns;
                scn.setCursorPosition(new TerminalPosition(25 + 18, 5 + (size - 1)));
                scn.setCharacter(25 + 18, 5 + (size - 1), new TextCharacter('?'));
                scn.refresh();
            }
        }
    }
}
