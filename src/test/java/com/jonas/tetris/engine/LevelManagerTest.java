package com.jonas.tetris.engine;

import org.junit.Test;
import static org.junit.Assert.*;

public class LevelManagerTest {

    @Test
    public void testLevelCalculation() {
        assertEquals(1, LevelManager.calculateLevel(0));
        assertEquals(1, LevelManager.calculateLevel(9));
        assertEquals(2, LevelManager.calculateLevel(10));
        assertEquals(2, LevelManager.calculateLevel(19));
        assertEquals(3, LevelManager.calculateLevel(20));
    }

    @Test
    public void testSpeedForLevel() {
        int speed1 = LevelManager.getSpeedForLevel(1);
        int speed2 = LevelManager.getSpeedForLevel(2);
        
        assertEquals(500, speed1);
        assertEquals(475, speed2);

        // Velocidade diminui com o nível
        assertTrue(speed1 > speed2);
    }

    @Test
    public void testLevelledUpDetection() {
        assertFalse(LevelManager.leveledUp(0, 5));
        assertTrue(LevelManager.leveledUp(9, 10));
        assertTrue(LevelManager.leveledUp(19, 20));
        assertFalse(LevelManager.leveledUp(20, 25));
    }

    @Test
    public void testHighLevelSpeedCap() {
        int speed20 = LevelManager.getSpeedForLevel(20);
        int speed99 = LevelManager.getSpeedForLevel(99);
        
        assertEquals(speed20, speed99); // Should be the same (capped)
    }

    @Test
    public void testLevel0Speed() {
        int speedInvalid = LevelManager.getSpeedForLevel(-1);
        int speed1 = LevelManager.getSpeedForLevel(1);
        
        assertEquals(speed1, speedInvalid); // Should default to level 1
    }
}
