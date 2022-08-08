package tablut;

import java.util.List;

/** A Player that automatically generates moves.
 *  @author Charlie Zhou
 */
class AI extends Player {

    /** A position-score magnitude indicating a win (for white if positive,
     *  black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A position-score magnitude indicating a forced win in a subsequent
     *  move.  This differs from WINNING_VALUE to avoid putting off wins. */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        _controller.reportMove(move);
        return move.toString();
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        _lastFoundMove = null;
        if (_myPiece.side() == Piece.WHITE) {
            findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
        } else {
            findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        if (depth == 0 || board.winner() == Piece.WHITE
                || board.winner() == Piece.BLACK) {
            return staticScore(board, depth);
        }
        if (sense == 1) {
            int value = -INFTY;
            List<Move> legalMoves = board.legalMoves(_myPiece);
            for (Move move : legalMoves) {
                board.makeMove(move);
                _myPiece = _myPiece.opponent();
                int currentMoveValue = findMove(board, depth - 1,
                        false, -sense, alpha, beta);
                _myPiece = _myPiece.opponent();
                board.undo();
                if (currentMoveValue > value) {
                    value = currentMoveValue;
                    if (saveMove) {
                        _lastFoundMove = move;
                    }
                }
                if (value > alpha) {
                    alpha = value;
                }
                if (alpha >= beta) {
                    break;
                }
            }
            return value;
        } else {
            int value = INFTY;
            List<Move> legalMoves = board.legalMoves(_myPiece);
            for (Move move : legalMoves) {
                board.makeMove(move);
                _myPiece = _myPiece.opponent();
                int currentMoveValue = findMove(board, depth - 1,
                        false, -sense, alpha, beta);
                _myPiece = _myPiece.opponent();
                board.undo();
                if (currentMoveValue < value) {
                    value = currentMoveValue;
                    if (saveMove) {
                        _lastFoundMove = move;
                    }
                }
                if (value < beta) {
                    beta = value;
                }
                if (alpha >= beta) {
                    break;
                }
            }
            return value;
        }
    }

    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private static int maxDepth(Board board) {
        return 4;
    }

    /**
     * Return a heuristic value for BOARD. (for white if positive,
     * black if negative).
     * @param board the board.
     * @param depth the depth.
     * @return the score.
     */
    private int staticScore(Board board, int depth) {
        Piece winner = board.winner();
        if (winner == Piece.WHITE) {
            return WINNING_VALUE + depth;
        } else if (winner == Piece.BLACK) {
            return -WINNING_VALUE - depth;
        }
        int score = 0;

        for (Square sq : Square.SQUARE_LIST) {
            Piece piece = board.get(sq);
            if (piece == Piece.KING) {
                for (int i = 0; i < 4; i++) {
                    int step = 1;
                    Move mv = Move.mv(sq, sq.rookMove(i, step));
                    while (mv != null && board.isLegal(mv)) {
                        step += 1;
                        mv = Move.mv(sq, sq.rookMove(i, step));
                    }
                    step -= 1;
                    if (step > 0) {
                        Square to = sq.rookMove(i, step);
                        if (to != null && to.isEdge()) {
                            return WILL_WIN_VALUE;
                        }
                    }
                }

                int count = board.surroundingPieces(sq, Piece.BLACK);
                score -= pow(8, count);
            } else if (piece == Piece.WHITE) {
                int count = board.surroundingPieces(sq, Piece.BLACK);
                score += 64;
                score -= pow(4, count);
            } else if (piece == Piece.BLACK) {
                int count = board.surroundingPieces(sq, Piece.WHITE);
                score -= 16;
                score += pow(2, count);
            }
        }
        return score;
    }

    /**
     * Calculate base ^ exp.
     * @param base the base.
     * @param exp the exponent.
     * @return base ^ exp.
     */
    private static int pow(int base, int exp) {
        int res = 1;
        for (int i = 0; i < exp; i++) {
            res *= base;
        }
        return res;
    }
}
