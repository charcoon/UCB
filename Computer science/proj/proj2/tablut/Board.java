package tablut;

import java.util.Arrays;
import java.util.Formatter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static tablut.Piece.*;
import static tablut.Square.*;
import static tablut.Utils.error;


/** The state of a Tablut Game.
 *  @author Charlie Zhou
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 9;

    /** The throne (or castle) square and its four surrounding squares.. */
    static final Square THRONE = sq(4, 4),
        NTHRONE = sq(4, 5),
        STHRONE = sq(4, 3),
        WTHRONE = sq(3, 4),
        ETHRONE = sq(5, 4);

    /** Initial positions of attackers. */
    static final Square[] INITIAL_ATTACKERS = {
        sq(0, 3), sq(0, 4), sq(0, 5), sq(1, 4),
        sq(8, 3), sq(8, 4), sq(8, 5), sq(7, 4),
        sq(3, 0), sq(4, 0), sq(5, 0), sq(4, 1),
        sq(3, 8), sq(4, 8), sq(5, 8), sq(4, 7)
    };

    /** Initial positions of defenders of the king. */
    static final Square[] INITIAL_DEFENDERS = {
        NTHRONE, ETHRONE, STHRONE, WTHRONE,
        sq(4, 6), sq(4, 2), sq(2, 4), sq(6, 4)
    };

    /**
     * The throne and its neighbors.
     */
    static final List<Square> THRONES = List.of(
            THRONE, NTHRONE, ETHRONE, STHRONE, WTHRONE
    );

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        if (model == this) {
            return;
        }
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(model._state[i], 0, this._state[i], 0, SIZE);
        }
        this._boardStack = new LinkedList<>(model._boardStack);
        this._moveLimit = model._moveLimit;
        this._turn = model._turn;
        this._winner = model._winner;
        this._repeated = model._repeated;
    }

    /** Clears the board to the initial position. */
    void init() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                put(EMPTY, sq(i, j));
            }
        }
        for (Square square : INITIAL_DEFENDERS) {
            put(WHITE, square);
        }
        for (Square square: INITIAL_ATTACKERS) {
            put(BLACK, square);
        }
        put(KING, THRONE);
        _turn = BLACK;
        _winner = null;
        _repeated = false;
        _moveLimit = -1;

        this._boardStack = new LinkedList<>();
    }

    /** Set the move limit to LIM.  It is an error if 2*LIM <= moveCount().
     *
     * @param n the move limit
     */
    void setMoveLimit(int n) {
        if (2 * n <= moveCount()) {
            throw error("bad limit argument");
        }
        _moveLimit = n;
    }

    /** Return a Piece representing whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the winner in the current position, or null if there is no winner
     *  yet. */
    Piece winner() {
        return _winner;
    }

    /** Returns true iff this is a win due to a repeated position. */
    boolean repeatedPosition() {
        return _repeated;
    }

    /** Record current position and set winner() next mover if the current
     *  position is a repeat. */
    private void checkRepeated() {
        for (Board board : _boardStack) {
            if (board.equals(this)) {
                _repeated = true;
                _winner = _turn;
                break;
            }
        }
    }

    /** Return the number of moves since the initial position that have not been
     *  undone. */
    int moveCount() {
        return _boardStack.size();
    }

    /** Return location of the king. */
    Square kingPosition() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (get(i, j) == KING) {
                    return sq(i, j);
                }
            }
        }
        return null;
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        return _state[col][row];
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        int col = s.col();
        int row = s.row();
        _state[col][row] = p;
    }

    /** Set square S to P and record for undoing. */
    final void revPut(Piece p, Square s) {
        put(p, s);
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, sq(col - 'a', row - '1'));
    }

    /** Return true iff FROM - TO is an unblocked rook move on the current
     *  board.  For this to be true, FROM-TO must be a rook move and the
     *  squares along it, other than FROM, must be empty. */
    boolean isUnblockedMove(Square from, Square to) {
        int direction = from.direction(to);
        for (Square step = from.rookMove(direction, 1);
             step != to;
             step = step.rookMove(direction, 1)) {
            assert step != null;
            if (get(step) != EMPTY) {
                return false;
            }
        }
        return true;
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return get(from).side() == _turn;
    }

    /** Return true iff FROM-TO is a valid move. */
    boolean isLegal(Square from, Square to) {
        /*
         * There are several rules:
         * - Only moves orthogonally.
         * - Destination should be empty.
         * - Don't jump over another piece.
         * - Pieces other than King can't land on throne.
         */
        if (!from.isRookMove(to)) {
            return false;
        }

        if (get(to) != EMPTY) {
            return false;
        }

        if (!isUnblockedMove(from, to)) {
            return false;
        }

        return to != THRONE || get(from) == KING;
    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to());
    }

    /** Move FROM-TO, assuming this is a legal move. */
    void makeMove(Square from, Square to) {
        assert isLegal(from, to);
        _boardStack.push(new Board(this));

        Piece originPiece = get(from);
        put(EMPTY, from);
        put(originPiece, to);

        postMove(to);
    }

    /**
     * The post-move actions.
     * @param to the destination square
     */
    void postMove(Square to) {
        _turn = _turn.opponent();

        capturePieces(to);
        if (isKingEscaped(to)) {
            _winner = WHITE;
        }
        checkRepeated();

        if (_winner == null
                && (_moveLimit != -1 && moveCount() >= _moveLimit * 2)) {
            _winner = _turn.opponent();
        }
    }

    /**
     * Checks if the king has escaped.
     * @param to the square moved.
     * @return if the king has escaped.
     */
    private boolean isKingEscaped(Square to) {
        Piece piece = get(to);
        return piece == KING && to.isEdge();
    }

    /**
     * Capture all pieces which should be captured in this move.
     * @param sq0 the square moved to.
     */
    private void capturePieces(Square sq0) {
        Piece piece0 = get(sq0);
        for (int i = 0; i < 4; i++) {
            Square sq2 = sq0.rookMove(i, 2);
            if (sq2 == null) {
                continue;
            }
            Square sq1 = sq0.between(sq2);
            Piece piece1 = get(sq1);

            if (piece1 == KING && THRONES.contains(sq1)) {
                if (isKingCaptured(sq1)) {
                    capture(sq0, sq2);
                }
            } else {
                if (isHostile(sq0, piece1.side())
                        && isHostile(sq2, piece1.side())) {
                    capture(sq0, sq2);
                }
            }
        }
    }

    /**
     * @param king The king's square
     * @return If the king has been captured.
     */
    private boolean isKingCaptured(Square king) {
        for (int j = 0; j < 4; j++) {
            Square other = king.rookMove(j, 1);
            if (other == null) {
                return false;
            }
            if (!isHostile(other, KING.side())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the square is hostile to the side.
     * @param sq the square
     * @param side the side
     * @return whether hostile
     */
    boolean isHostile(Square sq, Piece side) {
        Piece piece = get(sq);
        if (piece.opponent() == side) {
            return true;
        }
        if (sq == THRONE && piece == EMPTY) {
            return true;
        }
        if (sq == THRONE && surroundingPieces(sq, BLACK) >= 3) {
            return true;
        }
        return false;
    }

    /**
     * Calculates the count of surrounding pieces of specific side.
     * @param sq the square.
     * @param side the side.
     * @return the count.
     */
    int surroundingPieces(Square sq, Piece side) {
        int count = 0;
        for (int i = 0; i < 4; i++) {
            Square sq1 = sq.rookMove(i, 1);
            if (sq1 == null) {
                continue;
            }
            if (get(sq1).side() == side) {
                count += 1;
            }
        }
        return count;
    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to());
    }

    /** Capture the piece between SQ0 and SQ2, assuming a piece just moved to
     *  SQ0 and the necessary conditions are satisfied. */
    private void capture(Square sq0, Square sq2) {
        Square middle = sq0.between(sq2);
        if (get(middle) == KING) {
            _winner = BLACK;
        }
        put(EMPTY, middle);
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (!_boardStack.isEmpty()) {
            undoPosition();
        }
    }

    /** Remove record of current position in the set of positions encountered,
     *  unless it is a repeated position or we are at the first move. */
    private void undoPosition() {
        Board last = _boardStack.poll();
        copy(last);
    }

    /** Clear the undo stack and board-position counts. Does not modify the
     *  current position or win status. */
    void clearUndo() {
        this._boardStack.clear();
    }

    /** Return a new mutable list of all legal moves on the current board for
     *  SIDE (ignoring whose turn it is at the moment). */
    List<Move> legalMoves(Piece side) {
        List<Move> allMoves = new LinkedList<>();
        Set<Square> locations = pieceLocations(side);
        for (Square sq : locations) {
            allMoves.addAll(legalMoves(sq));
        }
        return allMoves;
    }


    /**
     * Return a new mutable list of all legal moves on the current board for
     * square (ignoring whose turn it is at the moment).
     * @param sq the square.
     * @return the moves.
     */
    private List<Move> legalMoves(Square sq) {
        List<Move> moves = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            int step = 1;
            Move mv = Move.mv(sq, sq.rookMove(i, step));
            while (mv != null && isLegal(mv)) {
                moves.add(mv);
                step += 1;
                mv = Move.mv(sq, sq.rookMove(i, step));
            }
        }
        return moves;
    }

    /** Return true iff SIDE has a legal move. */
    boolean hasMove(Piece side) {
        List<Move> moves = legalMoves(side);
        return !moves.isEmpty();
    }

    @Override
    public String toString() {
        return toString(true);
    }

    /** Return a text representation of this Board.  If COORDINATES, then row
     *  and column designations are included along the left and bottom sides.
     */
    String toString(boolean coordinates) {
        Formatter out = new Formatter();
        for (int r = SIZE - 1; r >= 0; r -= 1) {
            if (coordinates) {
                out.format("%2d", r + 1);
            } else {
                out.format("  ");
            }
            for (int c = 0; c < SIZE; c += 1) {
                out.format(" %s", get(c, r));
            }
            out.format("%n");
        }
        if (coordinates) {
            out.format("  ");
            for (char c = 'a'; c <= 'i'; c += 1) {
                out.format(" %c", c);
            }
            out.format("%n");
        }
        return out.toString();
    }

    /** Return the locations of all pieces on SIDE. */
    private HashSet<Square> pieceLocations(Piece side) {
        assert side != EMPTY;
        HashSet<Square> locations = new HashSet<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Square sq = sq(i, j);
                if (get(sq).side() == side) {
                    locations.add(sq);
                }
            }
        }
        return locations;
    }

    /** Return the contents of _board in the order of SQUARE_LIST as a sequence
     *  of characters: the toString values of the current turn and Pieces. */
    String encodedBoard() {
        char[] result = new char[Square.SQUARE_LIST.size() + 1];
        result[0] = turn().toString().charAt(0);
        for (Square sq : SQUARE_LIST) {
            result[sq.index() + 1] = get(sq).toString().charAt(0);
        }
        return new String(result);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Board board = (Board) o;
        return _turn == board._turn
                && Arrays.deepEquals(_state, board._state);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(_turn);
        result = result + Arrays.deepHashCode(_state);
        return result;
    }

    /** Piece whose turn it is (WHITE or BLACK). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;
    /** True when current board is a repeated position (ending the game). */
    private boolean _repeated;
    /**
     * Current state of board.
     */
    private Piece[][] _state = new Piece[SIZE][SIZE];

    /**
     * The move limit, -1 for unlimited.
     */
    private int _moveLimit = -1;

    /**
     * Stack to record all boards.
     * Note: no in-place board modifications.
     */
    private LinkedList<Board> _boardStack;
}
