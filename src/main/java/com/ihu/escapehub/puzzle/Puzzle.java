package com.ihu.escapehub.puzzle;

public interface Puzzle {
    String id();
    boolean isCorrect(String answer);
}