package tablut;

import ucb.gui2.Pad;

import java.util.concurrent.ArrayBlockingQueue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.event.MouseEvent;

import static tablut.Square.sq;

/** A widget that displays a Tablut game.
 *  @author Charlie Zhou
 */
class BoardWidget extends Pad {

    /* Parameters controlling sizes, speeds, colors, and fonts. */

    /** Squares on each side of the board. */
    static final int SIZE = Board.SIZE;

    /** Colors of empty squares, pieces, grid lines, and boundaries. */
    static final Color
        SQUARE_COLOR = new Color(238, 207, 161),
        THRONE_COLOR = new Color(180, 255, 180),
        ADJACENT_THRONE_COLOR = new Color(200, 220, 200),
        CLICKED_SQUARE_COLOR = new Color(255, 255, 100),
        GRID_LINE_COLOR = Color.black,
        WHITE_COLOR = Color.white,
        BLACK_COLOR = Color.black;

    /** Margins. */
    static final int
        OFFSET = BoardWidget.TWO,
        MARGIN = 16;

    /** Side of single square and of board (in pixels). */
    static final int
        SQUARE_SIDE = 30,
        PIECE_SIDE = 24,
        BOARD_SIDE = SQUARE_SIDE * SIZE
                + BoardWidget.TWO * OFFSET + MARGIN;

    /** The font in which to render the "K" in the king. */
    static final Font KING_FONT = new Font("Serif", Font.BOLD, 18);
    /** The font for labeling rows and columns. */
    static final Font ROW_COL_FONT = new Font("SanSerif", Font.PLAIN, 10);

    /** Squares adjacent to the throne. */
    static final Square[] ADJACENT_THRONE = {
        Board.NTHRONE, Board.ETHRONE, Board.STHRONE, Board.WTHRONE
    };
    /**
     * 8.
     */
    public static final int EIGHT = 8;
    /**
     * 20.
     */
    public static final int TWENTY = 20;
    /**
     * 3.
     */
    public static final int THREE = 3;
    /**
     * 1.
     */
    public static final int ONE = 1;
    /**
     * 2.
     */
    public static final int TWO = 2;

    /** A graphical representation of a Tablut board that sends commands
     *  derived from mouse clicks to COMMANDS.  */
    BoardWidget(ArrayBlockingQueue<String> commands) {
        _commands = commands;
        setMouseHandler("click", this::mouseClicked);
        setPreferredSize(BOARD_SIDE, BOARD_SIDE);
        _acceptingMoves = false;
    }

    /** Draw the bare board G.  */
    private void drawGrid(Graphics2D g) {
        g.setColor(SQUARE_COLOR);
        g.fillRect(0, 0, BOARD_SIDE, BOARD_SIDE);
        g.setColor(THRONE_COLOR);
        g.fillRect(cx(Board.THRONE), cy(Board.THRONE),
                   SQUARE_SIDE, SQUARE_SIDE);
        g.setColor(ADJACENT_THRONE_COLOR);
        for (Square sq : ADJACENT_THRONE) {
            g.fillRect(cx(sq), cy(sq), SQUARE_SIDE, SQUARE_SIDE);
        }
        g.setColor(GRID_LINE_COLOR);
        for (int k = 0; k <= SIZE; k += ONE) {
            g.drawLine(cx(0), cy(k - ONE), cx(SIZE), cy(k - ONE));
            g.drawLine(cx(k), cy(-ONE), cx(k), cy(SIZE - ONE));
        }

        for (int i = ONE; i <= SIZE; i++) {
            g.setFont(ROW_COL_FONT);
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(i),
                    6, BOARD_SIDE - SQUARE_SIDE * i);
        }
        for (int i = ONE; i <= SIZE; i++) {
            g.setFont(ROW_COL_FONT);
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf((char) ('a' + i - ONE)),
                    SQUARE_SIDE * i, BOARD_SIDE - 6);
        }
    }

    @Override
    public synchronized void paintComponent(Graphics2D g) {
        drawGrid(g);
        Square.SQUARE_LIST.iterator().forEachRemaining(s -> drawPiece(g, s));
    }

    /** Draw the contents of S on G. */
    private void drawPiece(Graphics2D g, Square s) {
        Piece piece = _board.get(s);
        if (s == _clicked && piece != Piece.EMPTY) {
            g.setColor(CLICKED_SQUARE_COLOR);
            g.fillRect(cx(s) + ONE, cy(s) + ONE,
                    SQUARE_SIDE - TWO, SQUARE_SIDE - TWO);
        }
        switch (piece) {
        case BLACK: {
            g.setColor(BLACK_COLOR);
            g.fillOval(cx(s) + THREE, cy(s) + THREE, PIECE_SIDE, PIECE_SIDE);
            break;
        }
        case WHITE: {
            g.setColor(WHITE_COLOR);
            g.fillOval(cx(s) + THREE, cy(s) + THREE, PIECE_SIDE, PIECE_SIDE);
            break;
        }
        case KING: {
            g.setFont(KING_FONT);
            g.setColor(Color.RED);
            g.drawString("K", cx(s) + EIGHT, cy(s) + TWENTY);
            break;
        }
        default: {
            break;
        }
        }
        if (piece != Piece.EMPTY) {
            g.setColor(Color.RED);
            g.drawOval(cx(s) + THREE, cy(s) + THREE, PIECE_SIDE, PIECE_SIDE);
        }
    }

    /** Handle a click on S. */
    private void click(Square s) {
        if (!_acceptingMoves) {
            return;
        }

        Piece piece = _board.get(s);
        if (piece == Piece.EMPTY && _clicked != null) {
            _commands.offer(Move.mv(_clicked, s).toString());
            _clicked = null;
        } else if (piece.side() == _board.turn()) {
            _clicked = s;
        } else {
            _clicked = null;
        }
        repaint();
    }

    /** Handle mouse click event E. */
    private synchronized void mouseClicked(String unused, MouseEvent e) {
        int xpos = e.getX(), ypos = e.getY();
        int x = (xpos - OFFSET - MARGIN) / SQUARE_SIDE,
            y = (OFFSET - ypos) / SQUARE_SIDE + SIZE - ONE;
        if (_acceptingMoves
            && x >= 0 && x < SIZE && y >= 0 && y < SIZE) {
            click(sq(x, y));
        }
    }

    /** Revise the displayed board according to BOARD. */
    synchronized void update(Board board) {
        _board.copy(board);
        repaint();
    }

    /** Turn on move collection iff COLLECTING, and clear any current
     *  partial selection.  When move collection is off, ignore clicks on
     *  the board. */
    void setMoveCollection(boolean collecting) {
        _acceptingMoves = collecting;
        repaint();
    }

    /** Return x-pixel coordinate of the left corners of column X
     *  relative to the upper-left corner of the board. */
    private int cx(int x) {
        return x * SQUARE_SIDE + OFFSET + MARGIN;
    }

    /** Return y-pixel coordinate of the upper corners of row Y
     *  relative to the upper-left corner of the board. */
    private int cy(int y) {
        return (SIZE - y - ONE) * SQUARE_SIDE + OFFSET;
    }

    /** Return x-pixel coordinate of the left corner of S
     *  relative to the upper-left corner of the board. */
    private int cx(Square s) {
        return cx(s.col());
    }

    /** Return y-pixel coordinate of the upper corner of S
     *  relative to the upper-left corner of the board. */
    private int cy(Square s) {
        return cy(s.row());
    }

    /** Queue on which to post move commands (from mouse clicks). */
    private ArrayBlockingQueue<String> _commands;
    /** Board being displayed. */
    private final Board _board = new Board();

    /** True iff accepting moves from user. */
    private boolean _acceptingMoves;

    /**
     * The clicked square.
     */
    private Square _clicked;

}
