package com.jonas.tetris.engine;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ScoreCalculatorTest {

    @Test
    public void testSingleLineScore() {
        int score = ScoreCalculator.calculateLinesCleared(1, 1);
        assertEquals(40, score);

        score = ScoreCalculator.calculateLinesCleared(1, 5);
        assertEquals(200, score);
    }

    @Test
    public void testDoubleLineScore() {
        int score = ScoreCalculator.calculateLinesCleared(2, 1);
        assertEquals(100, score);

        score = ScoreCalculator.calculateLinesCleared(2, 3);
        assertEquals(300, score);
    }

    @Test
    public void testTripleLineScore() {
        int score = ScoreCalculator.calculateLinesCleared(3, 1);
        assertEquals(300, score);

        score = ScoreCalculator.calculateLinesCleared(3, 4);
        assertEquals(1200, score);
    }

    @Test
    public void testTetrisScore() {
        int score = ScoreCalculator.calculateLinesCleared(4, 1);
        assertEquals(1200, score);

        score = ScoreCalculator.calculateLinesCleared(4, 2);
        assertEquals(2400, score);
    }

    @Test
    public void testInvalidLineCounts() {
        assertEquals(0, ScoreCalculator.calculateLinesCleared(0, 1));
        assertEquals(0, ScoreCalculator.calculateLinesCleared(5, 1));
        assertEquals(0, ScoreCalculator.calculateLinesCleared(-1, 1));
    }

    @Test
    public void testSoftDropScore() {
        assertEquals(1, ScoreCalculator.calculateSoftDrop(1));
        assertEquals(5, ScoreCalculator.calculateSoftDrop(5));
        assertEquals(20, ScoreCalculator.calculateSoftDrop(20));
    }

    @Test
    public void testHardDropScore() {
        assertEquals(2, ScoreCalculator.calculateHardDrop(1));
        assertEquals(10, ScoreCalculator.calculateHardDrop(5));
        assertEquals(40, ScoreCalculator.calculateHardDrop(20));
    }

    @Test
    public void testNegativeDropScore() {
        assertEquals(0, ScoreCalculator.calculateSoftDrop(-1));
        assertEquals(0, ScoreCalculator.calculateHardDrop(-1));
    }
}
