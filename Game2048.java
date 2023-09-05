package com.codegym.games.game2048;

import com.codegym.engine.cell.*;
import java.util.Arrays;

public class Game2048 extends Game {
    private static final int SIDE = 4;
    
    private int[][] gameField;
    
    private boolean isGameStopped = false;
    
    private int score = 0;
    
    public void initialize() {
        setScreenSize(SIDE, SIDE);
        createGame();
        drawScene();
    }
    
    private void createGame() {
        gameField = new int[SIDE][SIDE];
        for (int[] i : gameField) {
            for (int j : i) {
                i[j] = 0;
            }
        }
        createNewNumber();
        createNewNumber();
        
        score = 0;
        setScore(score);
    }
    
    private void drawScene() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                setCellColoredNumber(i, j, gameField[j][i]);
            }
        }
    }
    
    private void createNewNumber() {
        if (getMaxTileValue() == 2048) {
            win();
        } else {
            int x;
            int y;
            do {
                x = getRandomNumber(SIDE);
                y = getRandomNumber(SIDE);
            } while (gameField[x][y] != 0);
            
            if (getRandomNumber(10) == 9) {
                gameField[x][y] = 4;
            } else {
                gameField[x][y] = 2;
            }
        }
    }
    
    private void setCellColoredNumber(int x, int y, int value) {
        Color cellColor = getColorByValue(value);
        String displayVal = "";
        
        if (value != 0) {
            displayVal += value;
        }
        
        setCellValueEx(x, y, cellColor, displayVal);
        
    }
    
    private Color getColorByValue(int value) {
        switch (value) {
            case 2: return Color.BLUE;
            case 4: return Color.LIGHTBLUE;
            case 8: return Color.LIGHTGREEN;
            case 16: return Color.TEAL;
            case 32: return Color.GREEN;
            case 64: return Color.BROWN;
            case 128: return Color.ORANGE;
            case 256: return Color.PINK;
            case 512: return Color.PURPLE;
            case 1024: return Color.RED;
            case 2048: return Color.GOLD;
            default: return Color.LIGHTYELLOW;
        }
    }
    
    private boolean compressRow(int[] row) {
        int[] newRow = new int[SIDE];
        int newRowIndex = 0;
        
        for (int i : row) {
            if (i != 0) {
                newRow[newRowIndex] = i;
                newRowIndex++;
            }
        }
        
        if (newRowIndex != SIDE) {
            do {
                newRow[newRowIndex] = 0;
                newRowIndex++;
            } while (newRowIndex < SIDE);
        }
        
        boolean isCompressed = !Arrays.equals(row, newRow);
        
        for (int i = 0; i < SIDE; i++) {
        	row[i] = newRow[i];
        }
        
        return isCompressed;
        
    }
    
    private boolean mergeRow(int[] row) {
        boolean isMerged = false;
        for (int i = 0; i < SIDE - 1; i++) {
            if (row[i] == 0) { continue; }
            if (row[i] == row[i+1]) {
                row[i] += row[i+1];
                score += row[i];
                setScore(score);
                isMerged = true;
                row[i+1] = 0;
                i++;
            }
        }
        return isMerged;
    }
    
    public void setScore(int score) {
        
    }
    
    public void onKeyPress(Key key) {
        if (isGameStopped) {
            if (key == Key.SPACE) {
                isGameStopped = false;
                createGame();
                drawScene();
            }
        } else if (!canUserMove()) {
            gameOver();
        } else {
            switch (key) {
                case LEFT: moveLeft(); drawScene(); break;
                case RIGHT: moveRight(); drawScene(); break;
                case UP: moveUp(); drawScene(); break;
                case DOWN: moveDown(); drawScene(); break;
            }
        }
    }
    
    private void moveLeft() {
        boolean isChanged = false;
		for (int[] i : gameField) {
			if (compressRow(i)) {
				isChanged = true;
			}
			if (mergeRow(i)) {
				isChanged = true;
			}
			compressRow(i);
		}
        
        if (isChanged) {
            createNewNumber();
        }
    }
    
    private void moveRight() {
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
    }
    
    private void moveUp() {
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
    }
    
    private void moveDown() {
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }
    
    private void rotateClockwise() {
        gameField = getColumns(gameField);
        for (int[] i : gameField) {
            reverse(i);
        }
    }
    
    private void reverse(int[] row) {
    	for (int i = 0; i < SIDE / 2; i++) {
    		int j = row[i];
    		row[i] = row[SIDE - i - 1];
    		row[SIDE - i - 1] = j;
    	}
    }
    
    private int[][] getColumns(int[][] matrix) {
		int[][] colsFirst = new int[SIDE][SIDE];
    	for (int i = 0; i < SIDE; i++) {
			for (int j = 0; j < SIDE; j++) {
				colsFirst[i][j] = matrix[j][i];
			}
		}
    	
    	return colsFirst;
    }
    
    private int getMaxTileValue() {
        int largest = 0;
        for (int[] i : gameField) {
            for (int j : i) {
                if (j > largest) {
                    largest = j;
                }
            }
        }
        return largest;
    }
    
    private void win() {
        showMessageDialog(Color.BLACK, "2048! YOU WIN!", Color.WHITE, 60);
        isGameStopped = true;
    }
    
    private void gameOver() {
        showMessageDialog(Color.BLACK, "No moves left. YOU LOSE.", Color.WHITE, 60);
        isGameStopped = true;
    }
    
    private boolean canUserMove() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (gameField[i][j] == 0) {
                    return true;
                }
                if (i != SIDE - 1) {
                    if (gameField[i][j] == gameField[i+1][j]) {
                        return true;
                    }
                }
                if (j != SIDE - 1) {
                    if (gameField[i][j] == gameField[i][j+1]) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
}