package com.jonas.tetris.domain;

/**
 * Posição imutável no tabuleiro.
 * Record Java 15+ com x (coluna) e y (linha).
 */
public record Position(int x,int y){
/**
 * Valida se a posição está dentro dos limites do tabuleiro.
 */
public boolean isWithinBounds(int boardWidth,int boardHeight){return x>=0&&x<boardWidth&&y>=0&&y<boardHeight;}

/**
 * Desloca a posição.
 */
public Position translate(int dx,int dy){return new Position(x+dx,y+dy);}}
