package tablut;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BoardTest {

    private Board board = new Board();

    @Before
    public void setUp() throws Exception {
        board.init();
    }

    @Test
    public void testEqual() {
        Board newBoard = new Board(board);
        Assert.assertEquals(newBoard, board);

        board.makeMove(Move.mv("i6-f"));
        Assert.assertNotEquals(newBoard, board);

        newBoard.makeMove(Move.mv("i6-f"));
        Assert.assertEquals(newBoard, board);
    }

    @Test
    public void testLimit() {
        board.setMoveLimit(2);
        board.makeMove(Move.mv("i6-f"));
        board.makeMove(Move.mv("f5-2"));
        board.makeMove(Move.mv("i5-7"));
        board.makeMove(Move.mv("e5-f"));
        Assert.assertEquals(Piece.WHITE, board.winner());
    }

    @Test
    public void testRepeat() {
        board.makeMove(Move.mv("i6-g"));
        board.makeMove(Move.mv("g5-3"));
        board.makeMove(Move.mv("g6-i"));
        board.makeMove(Move.mv("g3-5"));
        Assert.assertEquals(Piece.BLACK, board.winner());
        Assert.assertTrue(board.repeatedPosition());
    }

    @Test
    public void testLegalMoves() {
        Assert.assertEquals(56, board.legalMoves(Piece.WHITE).size());
        Assert.assertEquals(80, board.legalMoves(Piece.BLACK).size());
    }

    @Test
    public void testMove() {
        Assert.assertEquals(0, board.moveCount());
        board.makeMove(Move.mv("i6-f"));
        Assert.assertEquals(Piece.BLACK, board.get(Square.sq("f6")));
        Assert.assertEquals(1, board.moveCount());
    }

    @Test
    public void testCapture() {
        board.makeMove(Move.mv("i6-f"));
        board.makeMove(Move.mv("e7-f"));
        Assert.assertEquals(Piece.WHITE, board.get(Square.sq("f7")));
        Assert.assertEquals(Piece.EMPTY, board.get(Square.sq("f6")));
    }

    @Test
    public void testCaptureKing() {
        board.makeMove(Move.mv("i6-g"));
        board.makeMove(Move.mv("f5-2"));
        board.makeMove(Move.mv("i4-g"));
        Assert.assertEquals(Piece.EMPTY, board.get(Square.sq("g5")));
        board.makeMove(Move.mv("e5-g"));
        Assert.assertEquals(Piece.KING, board.get(Square.sq("g5")));
        board.makeMove(Move.mv("g4-h"));
        board.makeMove(Move.mv("e6-d"));
        board.makeMove(Move.mv("h4-g"));
        Assert.assertEquals(Piece.EMPTY, board.get(Square.sq("g5")));
        Assert.assertEquals(Piece.BLACK, board.winner());
    }

    @Test
    public void testCaptureKing2() {
        board.makeMove(Move.mv("i6-f"));
        board.makeMove(Move.mv("f5-2"));
        board.makeMove(Move.mv("i5-7"));
        board.makeMove(Move.mv("e5-f"));
        Assert.assertEquals(Piece.KING, board.get(Square.sq("f5")));
        board.makeMove(Move.mv("i4-f"));
        Assert.assertEquals(Piece.KING, board.get(Square.sq("f5")));
        board.makeMove(Move.mv("g5-2"));
        board.makeMove(Move.mv("h5-g"));
        Assert.assertEquals(Piece.EMPTY, board.get(Square.sq("f5")));
        Assert.assertEquals(Piece.BLACK, board.winner());
    }

    @Test
    public void testHostile() {
        board.makeMove(Move.mv("i6-f"));
        board.makeMove(Move.mv("f5-2"));
        board.makeMove(Move.mv("i4-f"));
        board.makeMove(Move.mv("e4-b"));
        board.makeMove(Move.mv("f4-e"));
        board.makeMove(Move.mv("e6-b"));
        board.makeMove(Move.mv("f6-e"));
        board.makeMove(Move.mv("g5-9"));
        board.makeMove(Move.mv("h5-9"));
        board.makeMove(Move.mv("e5-g"));
        board.makeMove(Move.mv("d1-a"));
        board.makeMove(Move.mv("g5-3"));
        board.makeMove(Move.mv("i5-f"));
        board.makeMove(Move.mv("c5-1"));
        board.makeMove(Move.mv("a5-c"));
        Assert.assertEquals(Piece.EMPTY, board.get(Square.sq("d5")));
        board.makeMove(Move.mv("c1-d"));
        board.makeMove(Move.mv("f5-h"));
        board.makeMove(Move.mv("d1-5"));
        board.makeMove(Move.mv("h5-f"));
        Assert.assertEquals(Piece.WHITE, board.get(Square.sq("d5")));
        Assert.assertNull(board.winner());
    }


    @Test
    public void testHostile2() {
        board.makeMove(Move.mv("i6-f"));
        board.makeMove(Move.mv("f5-2"));
        board.makeMove(Move.mv("i4-f"));
        board.makeMove(Move.mv("e4-b"));
        board.makeMove(Move.mv("f4-e"));
        board.makeMove(Move.mv("e6-b"));
        board.makeMove(Move.mv("f6-e"));
        board.makeMove(Move.mv("g5-9"));
        board.makeMove(Move.mv("h5-f"));
        board.makeMove(Move.mv("c5-1"));
        Assert.assertEquals(Piece.WHITE, board.get(Square.sq("d5")));
        board.makeMove(Move.mv("a5-c"));
        Assert.assertEquals(Piece.EMPTY, board.get(Square.sq("d5")));
    }

    @Test
    public void testUndo() {
        Board backup = new Board(board);
        board.makeMove(Move.mv("i6-f"));
        Board backup2 = new Board(board);
        board.makeMove(Move.mv("f5-2"));
        Board backup3 = new Board(board);
        board.makeMove(Move.mv("i4-f"));
        board.undo();
        Assert.assertEquals(backup3, board);
        board.undo();
        Assert.assertEquals(backup2, board);
        board.undo();
        Assert.assertEquals(backup, board);
    }

    @Test
    public void testMove2() {
        board.makeMove(Move.mv("d1-4"));
        board.makeMove(Move.mv("c5-4"));
        board.makeMove(Move.mv("i4-g"));
        board.makeMove(Move.mv("e3-g"));
        board.makeMove(Move.mv("f1-4"));
        board.makeMove(Move.mv("g5-4"));
        board.makeMove(Move.mv("e2-3"));
        board.makeMove(Move.mv("e7-f"));
        board.makeMove(Move.mv("h5-g"));
        board.makeMove(Move.mv("e6-g"));
        board.makeMove(Move.mv("a6-e"));
        board.makeMove(Move.mv("f5-6"));
        board.makeMove(Move.mv("i5-g"));
        board.makeMove(Move.mv("g4-f"));
        board.makeMove(Move.mv("g5-f"));
        board.makeMove(Move.mv("f4-g"));
        board.makeMove(Move.mv("i6-5"));
        board.makeMove(Move.mv("g4-f"));
        board.makeMove(Move.mv("i5-f"));
        board.makeMove(Move.mv("g3-f"));
        board.makeMove(Move.mv("b5-c"));
        Assert.assertEquals(String.format(
                  " 9 - - - B B B - - -%n"
                + " 8 - - - - B - - - -%n"
                + " 7 - - - - - W - - -%n"
                + " 6 - - - - B W W - -%n"
                + " 5 B - B W K B - - -%n"
                + " 4 B - W - W W - - -%n"
                + " 3 - - - - B W - - -%n"
                + " 2 - - - - - - - - -%n"
                + " 1 - - - - B - - - -%n"
                + "   a b c d e f g h i%n"), board.toString());
    }
}
