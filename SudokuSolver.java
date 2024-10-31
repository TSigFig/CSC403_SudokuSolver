package TermProject;
import stdlib.*;
import algs13.Stack;

import java.util.*;

public class SudokuSolver {

    private final int BOX_SIZE = 3; // Length and width of the  box set
    private final int BOARD_SIZE = BOX_SIZE * BOX_SIZE; // Length and width of the board

    private int[][] sudokuBoard;
    private HashSet<Integer>[] rowSets;
    private HashSet<Integer>[] columnSets;
    private HashSet<Integer>[] boxSets;
    private Stack<int[]> backtrackStack;

    // Returns box index for boxSets from row and column index
    private int getBoxIndex(int row, int col) {
        return ((row / BOX_SIZE) * BOX_SIZE) + (col / BOX_SIZE);
    }

    // Returns 2d array index from flatten index
    private int[] getUnflattenIndex(int index) {
        int row = index / BOARD_SIZE;
        int col = index % BOARD_SIZE;
        return new int[] { row, col };
    }

    // Returns array index from 2d array position
    private int getFlatIndex(int row, int col) {
        return row * BOARD_SIZE + col;
    }

    // Deletes number at sudokuBoard[row, col] from sets and resets board to 0
    private void deleteNumberFromSets(int[][] board, int row, int col) {
        // Get value at position
        int delNum = board[row][col];

        if (delNum > 0) {
            // Remove value from sets
            rowSets[row].remove(delNum);
            columnSets[col].remove(delNum);
            boxSets[getBoxIndex(row, col)].remove(delNum);

            // Reset value at position
            board[row][col] = 0;
        }

        // StdOut.format("Removed: %d from sudokuBoard[%d, %d]", delNum, row, col);
    }

    // Declare and initialize Hash Sets for constraints
    private void initializeHashSets() {
        // Initialize hash sets for constraints
        rowSets = new HashSet[BOARD_SIZE];
        columnSets = new HashSet[BOARD_SIZE];
        boxSets = new HashSet[BOARD_SIZE];
    }

    // Function that empties/clears HashSets
    private void emptyHashSets() {
        // Make 9 new Hash sets for each constraint
        for (int i = 0; i < BOARD_SIZE; i++) {
            rowSets[i] = new HashSet<>();
            columnSets[i] = new HashSet<>();
            boxSets[i] = new HashSet<>();
        }
    }

    // Puzzle solver function using backtracking method with a stack
    public void solvePuzzle(int[][] board) {
        // Reset stack
        backtrackStack = new Stack<>();

        // StdOut.println("Starting Puzzle");

        // Initialize local position and starting number outside of while loop
        int[] rowCol = findNextZero(board,0, 0);
        int startingNumber = 0;
        int row;
        int col;

        while (rowCol != null) {
            row = rowCol[0];
            col = rowCol[1];
            // Find answer for empty cell
            int answer = findAnswer(row, col, startingNumber);

            if (answer > 0) {
                // Update solution to the board
                board[row][col] = answer;
                // Add solution to sets
                rowSets[row].add(answer);
                columnSets[col].add(answer);
                boxSets[getBoxIndex(row, col)].add(answer);
                // Push solution onto the stack
                backtrackStack.push(new int[] {row, col});

                // Update rowCol to next empty cell position and reset startingNumber to 0
                rowCol  = findNextZero(board, row, col);
                startingNumber = 0;
            }
            else if (answer == 0) {
                // findAnswer didn't find a possible solution
                // Update board to 0 in case there was an answer there before
                if (board[row][col] != 0) {
                    deleteNumberFromSets(board, row, col);
                }

                // Get last solution position
                rowCol = backtrackStack.pop();
                // Update startingNumber to last answer
                startingNumber = board[rowCol[0]][rowCol[1]];
                // Remove last solution from sets
                deleteNumberFromSets(board, rowCol[0], rowCol[1]);
            }
        }
        //StdOut.println("Finished Puzzle");
        //printFinalBoard();
    }

    // Returns valid answer for sudokuBoard[row, col]
    private int findAnswer(int row, int col, int startNum) {
        // Start loop at startNum for optimization while backtracking. startNum usually = 0 unless backtracking
        for (int i = startNum + 1; i <= BOARD_SIZE; i++) {
            if (!rowSets[row].contains(i) && !columnSets[col].contains(i) && !boxSets[getBoxIndex(row, col)].contains(i)) {
                //StdOut.format("Placing: %d at sudokuBoard[%d, %d]\n", i, row, col);
                return i;
            }
        }
        //StdOut.format("No valid answer for sudokuBoard[%d, %d] starting from number: %d\n", row, col, startNum);
        return 0; // Need to check for zero because that means no answer was found
    }

    // Returns position of next "zero"/empty cell in sudokuBoard
    private int[] findNextZero(int[][] board, int startRow, int startCol) {
        for (int row = startRow; row < BOARD_SIZE; row++) {
            // Starts at startCol only on the first startRow iteration, otherwise it goes back to 0
            if (row != startRow) {
                startCol = 0;
            }
            for (int col = startCol; col < BOARD_SIZE; col++) {
                if (board[row][col] == 0) {
                    //StdOut.format("Next empty cell found at: sudokuBoard[%d, %d]\n", row, col);
                    return new int[] {row, col};
                }
            }
        }
        //StdOut.println("No more empty cells");
        return null; // CHECK FOR NULL AFTER RETURN
    }

    // Checks if starting board is valid and follows the constraints
    private void checkIfValidBoard(int[][] board) {
        // Check for invalid row length
        if (board.length != BOARD_SIZE) {
            throw new IllegalArgumentException(String.format("Sudoku board must have %d rows, found %d rows", BOARD_SIZE, board.length));
        }

        // Clear hashsets
        emptyHashSets();

        for (int row = 0; row < BOARD_SIZE; row++) {
            // Check for invalid column length
            if (board[row].length != BOARD_SIZE) {
                throw new IllegalArgumentException(String.format("Sudoku board must have %d columns, found %d columns at row [%d]", BOARD_SIZE, board[row].length, row));
            }
            for (int col = 0; col < BOARD_SIZE; col++) {
                int num = board[row][col];
                // Check for invalid number
                if (num < 0 || num > BOARD_SIZE) {
                    throw new IllegalArgumentException(String.format("Invalid number %d at [%d, %d]", num, row, col));
                }
                // Check if the number is a duplicate in the sets
                if (rowSets[row].contains(num) || columnSets[col].contains(num) || boxSets[getBoxIndex(row, col)].contains(num)) {
                    throw new IllegalArgumentException(String.format("Duplicate number %d at [%d, %d]", num, row, col));
                }
                // Add to hashsets if num != 0
                if (num != 0) {
                    rowSets[row].add(num);
                    columnSets[col].add(num);
                    boxSets[getBoxIndex(row, col)].add(num);
                }
            }
        }
    }

    // Function that parses custom string input and returns a 2d int
    private int[][] parseCustomBoard(String[][] input) {
        int[][] tempBoard = new int[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                try {
                    int num = Integer.parseInt(input[row][col]);
                    tempBoard[row][col] = num;
                } catch (NumberFormatException e) {
                    throw new NumberFormatException(
                            String.format("Invalid input %s at [%d, %d]", e, row, col)
                    );
                }
            }
        }
        return tempBoard;
    }


    // Function that takes custom user puzzle from input
    // I WANT TO FIND A BETTER WAY OF HANDLING THIS
    public String[][] createUserSudokuPuzzle() {
        StdOut.println("For copy and paste help:");
        StdOut.println(
                "000000000\n000000000\n000000000\n000000000\n000000000\n000000000\n000000000\n000000000\n000000000");

        StdOut.println("Please enter a sudoku puzzle: ");
        String[][] stringBoard = new String[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            String[] input = StdIn.readLine().trim().split("");
            for (int col = 0; col < input.length; col++) {
                stringBoard[row][col] = input[col];
                //StdOut.format("%s", input[col]);
            }
            StdOut.println();
        }
        return stringBoard;
    }

    // Print full sudokuBoard
    public void printSudokuBoard(int[][] board) {
        StdOut.println("Here's the sudoku board: ");
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                StdOut.format("%d ", board[i][j]);
            }
            StdOut.println();
        }
    }

    // Function return count of unique solutions for the puzzle
    public int countUniqueSolutions(int[][] board) {
        // Reset stack
        backtrackStack = new Stack<>();

        // Initialize local position and starting number outside of while loop
        int[] rowCol = findNextZero(board,0, 0);
        int startingNumber = 0;
        int solutionCount = 0;
        int row;
        int col;

        while (rowCol != null) {
            row = rowCol[0];
            col = rowCol[1];
            // Find answer for empty cell
            int answer = findAnswer(row, col, startingNumber);

            if (answer > 0) {
                // Update solution to the board
                board[row][col] = answer;
                // Add solution to sets
                rowSets[row].add(answer);
                columnSets[col].add(answer);
                boxSets[getBoxIndex(row, col)].add(answer);
                // Push solution onto the stack
                backtrackStack.push(new int[] {row, col});

                // Update rowCol to next empty cell position and reset startingNumber to 0
                rowCol  = findNextZero(board, row, col);
                startingNumber = 0;

                // Check if puzzle is solved
                if (rowCol == null) {
                    //printSudokuBoard(board);
                    solutionCount++;
                    StdOut.format("Solution found: %d\n", solutionCount);
                    if (solutionCount > 1) {
                        return solutionCount;
                    }
                    // Backtrack and search for another solution
                    rowCol = backtrackStack.pop();
                    row = rowCol[0];
                    col = rowCol[1];
                    startingNumber = board[row][col];
                    deleteNumberFromSets(board, row, col);
                }
            }
            else if (answer == 0) {
                // findAnswer didn't find a possible solution
                // Update board to 0 in case there was an answer there before
                deleteNumberFromSets(board, row, col);

                if (!backtrackStack.isEmpty()) {
                    // Get last solution position
                    rowCol = backtrackStack.pop();
                    // Update startingNumber to last answer
                    startingNumber = board[rowCol[0]][rowCol[1]];
                    // Remove last solution from sets
                    deleteNumberFromSets(board, rowCol[0], rowCol[1]);
                } else {
                    //StdOut.println("Break");
                    break;
                }
            }
        }
        return solutionCount;
    }

    private int[][] deepCopyBoard(int[][] original) {
        int[][] copy = new int[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }

    private int findRandomAnswer(int row, int col) {
        Random rand = new Random();
        List<Integer> possibleAnswers = new ArrayList<>();
        for (int i = 1; i <= BOARD_SIZE; i++) {
            if (!rowSets[row].contains(i) && !columnSets[col].contains(i) && !boxSets[getBoxIndex(row, col)].contains(i)) {
                possibleAnswers.add(i);
            }
        }
        if (possibleAnswers.isEmpty()) return 0;
        return possibleAnswers.get(rand.nextInt(possibleAnswers.size()));
    }

    private void testingSolutionCounts() {
        int [][] board = new int[][] {
                {8, 0, 0, 0, 0, 9, 1, 0, 0},
                {0, 9, 7, 0, 0, 1, 0, 5, 0},
                {4, 0, 0, 2, 5, 0, 0, 7, 3},
                {9, 0, 0, 0, 6, 3, 0, 8, 0},
                {0, 7, 4, 0, 0, 0, 3, 6, 0},
                {0, 8, 0, 4, 9, 0, 0, 0, 1},
                {2, 4, 0, 0, 8, 5, 0, 0, 7},
                {0, 3, 0, 9, 0, 0, 4, 1, 0},
                {0, 0, 5, 3, 0, 0, 0, 0, 8}};
        checkIfValidBoard(board);
        int solution = countUniqueSolutions(board);
        StdOut.format("Solutions: %d\n", solution);
        board = new int[][] {
                {0, 1, 0, 4, 0, 5, 0, 0, 0},
                {9, 0, 3, 0, 0, 0, 8, 0, 0},
                {4, 0, 0, 0, 7, 0, 0, 1, 3},
                {0, 0, 4, 5, 6, 0, 3, 0, 0},
                {1, 6, 0, 0, 0, 0, 0, 8, 5},
                {0, 0, 5, 0, 8, 2, 6, 0, 0},
                {6, 2, 0, 0, 5, 0, 0, 0, 8},
                {0, 0, 1, 0, 0, 0, 5, 0, 2},
                {0, 0, 0, 9, 0, 8, 0, 3, 0}};
        checkIfValidBoard(board);
        solution = countUniqueSolutions(board);
        StdOut.format("Solutions: %d\n", solution);
        //StdOut.println("Generating random unique board");
        //generateRandomSolution();

    }

    public static void main (String[] args) {
        SudokuSolver sudokuSolver = new SudokuSolver();
        sudokuSolver.initializeHashSets();
        sudokuSolver.testingPuzzles();
        sudokuSolver.testingCustomPuzzles();
        // FINISHED EVERYTHING ABOVE | NOW TESTING GENERATION BELOW
        sudokuSolver.testingSolutionCounts();
    }

    // Function that sends starting board and the answer to the test function
    public void testingPuzzles() {
        // 3 easy puzzles
        testPuzzle(new int[][] {
                {8, 0, 0, 0, 0, 9, 1, 0, 0},
                {0, 9, 7, 0, 0, 1, 0, 5, 0},
                {4, 0, 0, 2, 5, 0, 0, 7, 3},
                {9, 0, 0, 0, 6, 3, 0, 8, 0},
                {0, 7, 4, 0, 0, 0, 3, 6, 0},
                {0, 8, 0, 4, 9, 0, 0, 0, 1},
                {2, 4, 0, 0, 8, 5, 0, 0, 7},
                {0, 3, 0, 9, 0, 0, 4, 1, 0},
                {0, 0, 5, 3, 0, 0, 0, 0, 8}
        }, new int[][] {
                {8, 5, 2, 7, 3, 9, 1, 4, 6},
                {3, 9, 7, 6, 4, 1, 8, 5, 2},
                {4, 1, 6, 2, 5, 8, 9, 7, 3},
                {9, 2, 1, 5, 6, 3, 7, 8, 4},
                {5, 7, 4, 8, 1, 2, 3, 6, 9},
                {6, 8, 3, 4, 9, 7, 5, 2, 1},
                {2, 4, 9, 1, 8, 5, 6, 3, 7},
                {7, 3, 8, 9, 2, 6, 4, 1, 5},
                {1, 6, 5, 3, 7, 4, 2, 9, 8}
        }); // Puzzle (1) in sudoku book
        testPuzzle(new int[][] {
                {0, 0, 4, 0, 1, 0, 0, 3, 0},
                {6, 0, 0, 0, 0, 0, 8, 1, 0},
                {0, 0, 1, 8, 6, 0, 0, 9, 0},
                {8, 2, 6, 4, 0, 0, 0, 0, 9},
                {5, 0, 0, 2, 3, 6, 0, 0, 8},
                {3, 0, 0, 0, 0, 8, 4, 2, 6},
                {0, 9, 0, 0, 8, 4, 7, 0, 0},
                {0, 6, 5, 0, 0, 0, 0, 0, 3},
                {0, 3, 0, 0, 9, 0, 2, 0, 0}
        }, new int[][] {
                {9, 8, 4, 5, 1, 2, 6, 3, 7},
                {6, 5, 3, 7, 4, 9, 8, 1, 2},
                {2, 7, 1, 8, 6, 3, 5, 9, 4},
                {8, 2, 6, 4, 7, 1, 3, 5, 9},
                {5, 4, 9, 2, 3, 6, 1, 7, 8},
                {3, 1, 7, 9, 5, 8, 4, 2, 6},
                {1, 9, 2, 3, 8, 4, 7, 6, 5},
                {4, 6, 5, 1, 2, 7, 9, 8, 3},
                {7, 3, 8, 6, 9, 5, 2, 4, 1}
        }); // Puzzle (2) in sudoku book
        testPuzzle(new int[][] {
                {6, 0, 0, 0, 0, 0, 9, 2, 0},
                {2, 0, 0, 5, 0, 3, 0, 8, 0},
                {9, 1, 8, 6, 0, 0, 7, 0, 0},
                {0, 0, 0, 1, 0, 0, 0, 4, 0},
                {3, 0, 6, 0, 8, 0, 2, 0, 5},
                {0, 8, 0, 0, 0, 5, 0, 0, 0},
                {0, 0, 9, 0, 0, 4, 1, 7, 8},
                {0, 7, 0, 3, 0, 1, 0, 0, 6},
                {0, 6, 1, 0, 0, 0, 0, 0, 2}
        }, new int[][] {
                {6, 5, 3, 7, 1, 8, 9, 2, 4},
                {2, 4, 7, 5, 9, 3, 6, 8, 1},
                {9, 1, 8, 6, 4, 2, 7, 5, 3},
                {7, 2, 5, 1, 3, 6, 8, 4, 9},
                {3, 9, 6, 4, 8, 7, 2, 1, 5},
                {1, 8, 4, 9, 2, 5, 3, 6, 7},
                {5, 3, 9, 2, 6, 4, 1, 7, 8},
                {8, 7, 2, 3, 5, 1, 4, 9, 6},
                {4, 6, 1, 8, 7, 9, 5, 3, 2}
        }); // Puzzle (3) in sudoku book

        // 3 medium puzzles
        testPuzzle(new int[][] {
                {0, 0, 6, 0, 9, 0, 0, 0, 3},
                {9, 0, 0, 0, 0, 3, 0, 2, 8},
                {0, 1, 0, 0, 2, 8, 0, 0, 0},
                {6, 0, 8, 3, 0, 0, 2, 0, 0},
                {0, 0, 7, 2, 0, 1, 6, 0, 0},
                {0, 0, 2, 0, 0, 6, 3, 0, 5},
                {0, 0, 0, 8, 4, 0, 0, 5, 0},
                {8, 2, 0, 5, 0, 0, 0, 0, 9},
                {4, 0, 0, 0, 6, 0, 8, 0, 0}
        }, new int[][] {
                {2, 8, 6, 4, 9, 5, 7, 1, 3},
                {9, 7, 4, 6, 1, 3, 5, 2, 8},
                {3, 1, 5, 7, 2, 8, 9, 4, 6},
                {6, 9, 8, 3, 5, 4, 2, 7, 1},
                {5, 3, 7, 2, 8, 1, 6, 9, 4},
                {1, 4, 2, 9, 7, 6, 3, 8, 5},
                {7, 6, 3, 8, 4, 9, 1, 5, 2},
                {8, 2, 1, 5, 3, 7, 4, 6, 9},
                {4, 5, 9, 1, 6, 2, 8, 3, 7}
        }); // Puzzle (42) in sudoku book
        testPuzzle(new int[][] {
                {0, 0, 2, 6, 1, 0, 0, 0, 9},
                {0, 4, 8, 0, 0, 0, 0, 5, 0},
                {0, 1, 0, 0, 0, 5, 2, 0, 0},
                {5, 0, 0, 0, 0, 1, 0, 8, 0},
                {8, 0, 0, 9, 0, 2, 0, 0, 7},
                {0, 9, 0, 8, 0, 0, 0, 0, 4},
                {0, 0, 9, 5, 0, 0, 0, 2, 0},
                {0, 2, 0, 0, 0, 0, 6, 3, 0},
                {6, 0, 0, 0, 2, 3, 7, 0, 0}
        }, new int[][] {
                {3, 5, 2, 6, 1, 4, 8, 7, 9},
                {7, 4, 8, 2, 3, 9, 1, 5, 6},
                {9, 1, 6, 7, 8, 5, 2, 4, 3},
                {5, 7, 4, 3, 6, 1, 9, 8, 2},
                {8, 6, 3, 9, 4, 2, 5, 1, 7},
                {2, 9, 1, 8, 5, 7, 3, 6, 4},
                {1, 3, 9, 5, 7, 6, 4, 2, 8},
                {4, 2, 7, 1, 9, 8, 6, 3, 5},
                {6, 8, 5, 4, 2, 3, 7, 9, 1}
        }); // Puzzle (44) in sudoku book
        testPuzzle(new int[][] {
                {0, 1, 0, 4, 0, 5, 0, 0, 0},
                {9, 0, 3, 0, 0, 0, 8, 0, 0},
                {4, 0, 0, 0, 7, 0, 0, 1, 3},
                {0, 0, 4, 5, 6, 0, 3, 0, 0},
                {1, 6, 0, 0, 0, 0, 0, 8, 5},
                {0, 0, 5, 0, 8, 2, 6, 0, 0},
                {6, 2, 0, 0, 5, 0, 0, 0, 8},
                {0, 0, 1, 0, 0, 0, 5, 0, 2},
                {0, 0, 0, 9, 0, 8, 0, 3, 0}
        }, new int[][] {
                {2, 1, 8, 4, 3, 5, 9, 6, 7},
                {9, 7, 3, 2, 1, 6, 8, 5, 4},
                {4, 5, 6, 8, 7, 9, 2, 1, 3},
                {7, 8, 4, 5, 6, 1, 3, 2, 9},
                {1, 6, 2, 3, 9, 4, 7, 8, 5},
                {3, 9, 5, 7, 8, 2, 6, 4, 1},
                {6, 2, 9, 1, 5, 3, 4, 7, 8},
                {8, 3, 1, 6, 4, 7, 5, 9, 2},
                {5, 4, 7, 9, 2, 8, 1, 3, 6}
        }); // Puzzle (48) in sudoku book

        // 3 hard puzzles
        testPuzzle(new int[][] {
                {0, 1, 0, 0, 0, 4, 8, 0, 0},
                {0, 0, 0, 5, 0, 9, 1, 2, 0},
                {2, 0, 8, 0, 6, 0, 0, 0, 4},
                {9, 0, 0, 0, 0, 6, 0, 0, 0},
                {0, 6, 0, 0, 5, 0, 0, 4, 0},
                {0, 0, 0, 8, 0, 0, 0, 0, 7},
                {3, 0, 0, 0, 7, 0, 4, 0, 1},
                {0, 8, 2, 9, 0, 5, 0, 0, 0},
                {0, 0, 4, 3, 0, 0, 0, 9, 0}
        },new int[][] {
                {5, 1, 6, 2, 3, 4, 8, 7, 9},
                {7, 4, 3, 5, 8, 9, 1, 2, 6},
                {2, 9, 8, 1, 6, 7, 3, 5, 4},
                {9, 3, 7, 4, 2, 6, 5, 1, 8},
                {8, 6, 1, 7, 5, 3, 9, 4, 2},
                {4, 2, 5, 8, 9, 1, 6, 3, 7},
                {3, 5, 9, 6, 7, 2, 4, 8, 1},
                {1, 8, 2, 9, 4, 5, 7, 6, 3},
                {6, 7, 4, 3, 1, 8, 2, 9, 5}
        }); // Puzzle (163) in sudoku book
        testPuzzle(new int[][] {
                {0, 0, 0, 0, 4, 0, 0, 0, 5},
                {6, 0, 9, 0, 0, 0, 0, 8, 0},
                {0, 0, 0, 9, 0, 8, 3, 0, 0},
                {0, 0, 0, 0, 5, 1, 0, 0, 3},
                {4, 0, 0, 0, 0, 0, 0, 0, 2},
                {3, 0, 0, 8, 6, 0, 0, 0, 0},
                {0, 0, 3, 5, 0, 2, 0, 0, 0},
                {0, 4, 0, 0, 0, 0, 2, 0, 1},
                {7, 0, 0, 0, 8, 0, 0, 0, 0}
        }, new int[][] {
                {2, 8, 7, 3, 4, 6, 1, 9, 5},
                {6, 3, 9, 1, 2, 5, 7, 8, 4},
                {1, 5, 4, 9, 7, 8, 3, 2, 6},
                {8, 7, 6, 2, 5, 1, 9, 4, 3},
                {4, 1, 5, 7, 3, 9, 8, 6, 2},
                {3, 9, 2, 8, 6, 4, 5, 1, 7},
                {9, 6, 3, 5, 1, 2, 4, 7, 8},
                {5, 4, 8, 6, 9, 7, 2, 3, 1},
                {7, 2, 1, 4, 8, 3, 6, 5, 9}
        }); // Puzzle (164) in sudoku book
        testPuzzle(new int[][] {
                {0, 6, 0, 2, 0, 0, 0, 0, 5},
                {0, 0, 9, 0, 0, 3, 8, 0, 0},
                {7, 0, 0, 0, 0, 0, 0, 9, 0},
                {0, 0, 3, 1, 0, 0, 4, 0, 7},
                {0, 0, 0, 3, 0, 4, 0, 0, 0},
                {6, 0, 8, 0, 0, 5, 1, 0, 0},
                {0, 1, 0, 0, 0, 0, 0, 0, 3},
                {0, 0, 6, 4, 0, 0, 9, 0, 0},
                {4, 0, 0, 0, 0, 1, 0, 7, 0}
        }, new int[][] {
                {3, 6, 4, 2, 8, 9, 7, 1, 5},
                {1, 5, 9, 7, 4, 3, 8, 2, 6},
                {7, 8, 2, 5, 1, 6, 3, 9, 4},
                {5, 9, 3, 1, 2, 8, 4, 6, 7},
                {2, 7, 1, 3, 6, 4, 5, 8, 9},
                {6, 4, 8, 9, 7, 5, 1, 3, 2},
                {9, 1, 7, 8, 5, 2, 6, 4, 3},
                {8, 2, 6, 4, 3, 7, 9, 5, 1},
                {4, 3, 5, 6, 9, 1, 2, 7, 8}
        }); // Puzzle (166) in sudoku book

        // 4 invalid puzzles
        testInvalidPuzzle(new int[][] {
                {0, 6, 0, 2, 0, 0, 0, 0, 5},
                {0, 0, 9, 0, 0, 3, 8, 0, 0},
                {7, 0, 0, 0, 0, 0, 0, 9, 0},
                {0, 0, 3, 1, 0, 0, 4, 0, 7}
        }); // Not enough rows
        testInvalidPuzzle(new int[][] {
                {0, 6, 0, 2, 0, 0, 0, 0, 5},
                {0, 0, 9, 0, 0, 3, 8, 0, 0},
                {7, 0, 0, 0, 0, 0, 0, 9, 0},
                {0, 0, 3, 1, 0, 0, 4, 0, 7},
                {0, 0, 0, 3, 0, 4, 0, 0, 0},
                {6, 0, 8, 0, 0, 5, 1, 0, 0},
                {0, 1, 0, 0, 0, 0, 0, 0, 3},
                {0, 0, 6, 4, 0, 0, 9, 0, 0},
                {4, 0, 0, 0, 0, 1, 0, 7}
        }); // Not enough columns
        testInvalidPuzzle(new int[][] {
                {0, 6, 0, 2, 0, 0, 0, 0, 5},
                {0, 0, 9, 0, 0, 3, 8, 0, 0},
                {7, 0, 0, 0, 0, 0, 0, 9, 0},
                {0, 0, 3, 1, 20, 0, 4, 0, 7},
                {0, 0, 0, 3, 0, 4, 0, 0, 0},
                {6, 0, 8, 0, 0, 5, 1, 0, 0},
                {0, 1, 0, 0, 0, 0, 0, 0, 3},
                {0, 0, 6, 4, 0, 0, 9, 0, 0},
                {4, 0, 0, 0, 0, 1, 0, 7, 0}
        }); // Number out of range > BOARD_SIZE
        testInvalidPuzzle(new int[][] {
                {0, 6, 0, 2, 0, 0, 0, 0, 5},
                {0, 0, 9, 0, 0, 3, 8, 0, 0},
                {7, 0, 0, 0, 0, 0, 0, 9, 0},
                {0, 0, 3, 1, -1, 0, 4, 0, 7},
                {0, 0, 0, 3, 0, 4, 0, 0, 0},
                {6, 0, 8, 0, 0, 5, 1, 0, 0},
                {0, 1, 0, 0, 0, 0, 0, 0, 3},
                {0, 0, 6, 4, 0, 0, 9, 0, 0},
                {4, 0, 0, 0, 0, 1, 0, 7, 0}
        }); // Number out of range < BOARD_SIZE
    }

    // Function that takes the starting sudoku board and checks if my application gets the right answer. Print's if it fails
    private void testPuzzle(int[][] startBoard, int[][] expectedBoard) {
        checkIfValidBoard(startBoard);
        //addBoardToHashSets(startBoard);
        solvePuzzle(startBoard);
        sudokuBoard = startBoard;
        if (!Arrays.deepEquals(expectedBoard, sudokuBoard)) {
            StdOut.format("Failed Puzzle: Expecting (%s), Actual (%s)\n", Arrays.deepToString(expectedBoard), Arrays.deepToString(sudokuBoard));
        }
    }

    // Function that tests invalid starting boards, catches IllegalArgumentException
    private void testInvalidPuzzle(int[][] startBoard) {
        try {
            checkIfValidBoard(startBoard);
            StdOut.println("Failed Puzzle: Did not catch IllegalArgumentException");
        } catch (IllegalArgumentException d) {
            // DO NOTHING
            //StdOut.println("Caught IllegalArgumentException");
        }
    }

    // Function that tests strings as the starting board for custom puzzles
    public void testingCustomPuzzles() {
        // 1 easy puzzle
        testCustomStringPuzzle(new String[][] {
                {"8", "0", "0", "0", "0", "9", "1", "0", "0"},
                {"0", "9", "7", "0", "0", "1", "0", "5", "0"},
                {"4", "0", "0", "2", "5", "0", "0", "7", "3"},
                {"9", "0", "0", "0", "6", "3", "0", "8", "0"},
                {"0", "7", "4", "0", "0", "0", "3", "6", "0"},
                {"0", "8", "0", "4", "9", "0", "0", "0", "1"},
                {"2", "4", "0", "0", "8", "5", "0", "0", "7"},
                {"0", "3", "0", "9", "0", "0", "4", "1", "0"},
                {"0", "0", "5", "3", "0", "0", "0", "0", "8"}
        }, new int[][] {
                {8, 5, 2, 7, 3, 9, 1, 4, 6},
                {3, 9, 7, 6, 4, 1, 8, 5, 2},
                {4, 1, 6, 2, 5, 8, 9, 7, 3},
                {9, 2, 1, 5, 6, 3, 7, 8, 4},
                {5, 7, 4, 8, 1, 2, 3, 6, 9},
                {6, 8, 3, 4, 9, 7, 5, 2, 1},
                {2, 4, 9, 1, 8, 5, 6, 3, 7},
                {7, 3, 8, 9, 2, 6, 4, 1, 5},
                {1, 6, 5, 3, 7, 4, 2, 9, 8}
        }); // Puzzle (1) in sudoku book

        // 1 medium puzzle
        testCustomStringPuzzle(new String[][] {
                {"0", "0", "6", "0", "9", "0", "0", "0", "3"},
                {"9", "0", "0", "0", "0", "3", "0", "2", "8"},
                {"0", "1", "0", "0", "2", "8", "0", "0", "0"},
                {"6", "0", "8", "3", "0", "0", "2", "0", "0"},
                {"0", "0", "7", "2", "0", "1", "6", "0", "0"},
                {"0", "0", "2", "0", "0", "6", "3", "0", "5"},
                {"0", "0", "0", "8", "4", "0", "0", "5", "0"},
                {"8", "2", "0", "5", "0", "0", "0", "0", "9"},
                {"4", "0", "0", "0", "6", "0", "8", "0", "0"}
        }, new int[][]{
                {2, 8, 6, 4, 9, 5, 7, 1, 3},
                {9, 7, 4, 6, 1, 3, 5, 2, 8},
                {3, 1, 5, 7, 2, 8, 9, 4, 6},
                {6, 9, 8, 3, 5, 4, 2, 7, 1},
                {5, 3, 7, 2, 8, 1, 6, 9, 4},
                {1, 4, 2, 9, 7, 6, 3, 8, 5},
                {7, 6, 3, 8, 4, 9, 1, 5, 2},
                {8, 2, 1, 5, 3, 7, 4, 6, 9},
                {4, 5, 9, 1, 6, 2, 8, 3, 7}
        }); // Puzzle (42) in sudoku book

        // 1 hard puzzle
        testCustomStringPuzzle(new String[][] {
                {"0", "6", "0", "2", "0", "0", "0", "0", "5"},
                {"0", "0", "9", "0", "0", "3", "8", "0", "0"},
                {"7", "0", "0", "0", "0", "0", "0", "9", "0"},
                {"0", "0", "3", "1", "0", "0", "4", "0", "7"},
                {"0", "0", "0", "3", "0", "4", "0", "0", "0"},
                {"6", "0", "8", "0", "0", "5", "1", "0", "0"},
                {"0", "1", "0", "0", "0", "0", "0", "0", "3"},
                {"0", "0", "6", "4", "0", "0", "9", "0", "0"},
                {"4", "0", "0", "0", "0", "1", "0", "7", "0"}
        }, new int[][] {
                {3, 6, 4, 2, 8, 9, 7, 1, 5},
                {1, 5, 9, 7, 4, 3, 8, 2, 6},
                {7, 8, 2, 5, 1, 6, 3, 9, 4},
                {5, 9, 3, 1, 2, 8, 4, 6, 7},
                {2, 7, 1, 3, 6, 4, 5, 8, 9},
                {6, 4, 8, 9, 7, 5, 1, 3, 2},
                {9, 1, 7, 8, 5, 2, 6, 4, 3},
                {8, 2, 6, 4, 3, 7, 9, 5, 1},
                {4, 3, 5, 6, 9, 1, 2, 7, 8}
        }); // Puzzle (166) in sudoku book

        testInvalidCustomPuzzle(new String[][] {
                {"0", "6", "0", "2", "0", "0", "0", "0", "5"},
                {"0", "0", "9", "0", "0", "3", "8", "0", "0"},
                {"7", "0", "0", "0", "0", "0", "0", "9", "0"},
                {"0", "0", "3", "1", "0", "0", "4", "0", "7"},
                {"0", "0", "0", "3", "L", "4", "0", "0", "0"},
                {"6", "0", "8", "0", "0", "5", "1", "0", "0"},
                {"0", "1", "0", "0", "0", "0", "0", "0", "3"},
                {"0", "0", "6", "4", "0", "0", "9", "0", "0"},
                {"4", "0", "0", "0", "0", "1", "0", "7", "0"}
        }); // parseInt a letter at the middle[4,4]
    }

    // Function that tests custom string boards and checks if it gets the expected answer
    private void testCustomStringPuzzle(String[][] input, int[][] expectedBoard) {
        int[][] parsedInput = parseCustomBoard(input);
        checkIfValidBoard(parsedInput);
        solvePuzzle(parsedInput);
        sudokuBoard = parsedInput;
        if (!Arrays.deepEquals(expectedBoard, sudokuBoard)) {
            StdOut.format("Failed Puzzle: Expecting (%s), Actual (%s)\n", Arrays.deepToString(expectedBoard), Arrays.deepToString(sudokuBoard));
        }
    }

    private void testInvalidCustomPuzzle(String[][] input) {
        try {
            int[][] parsedInput = parseCustomBoard(input);
            StdOut.println("Failed Puzzle: Did not catch NumberFormatException");
        } catch (NumberFormatException e){
            // DO NOTHING
            //StdOut.println("Caught NumberFormatException");
        }
    }
}


// OLD CODE -----------------------------------------------------------------------
// Custom Sudoku board creator. Makes sure user inputs a valid board
//public void createSudokuBoard() {
//    // Initialize hash sets, and board
//    initializeHashSets();
//    emptyHashSets();
//    sudokuBoard = new int[BOARD_SIZE][BOARD_SIZE];
//
//    StdOut.println("0 0 0 0 0 0 0 0 0   for copy and paste help");
//    StdOut.println("Please input a valid Sudoku Puzzle (row by row, 0 = empty cell)");
//
//    for (int row = 0; row < BOARD_SIZE; row++) {
//        backtrackStack = new Stack<>();
//        boolean isValid = false;
//        while (!isValid) {
//            StdOut.println("Enter 9 numbers for row " + (row + 1) + ":");
//            String input = StdIn.readLine().trim();
//            String[] numbers = input.split("\\s+");
//
//            if (numbers.length != 9) {
//                StdOut.println("Invalid input! Please enter exactly 9 numbers");
//                continue;
//            }
//
//            boolean allValid = true;
//            for (int col = 0; col < BOARD_SIZE; col++) {
//                try {
//                    int num = Integer.parseInt(numbers[col]);
//                    if (num < 0 || num > 9) {
//                        StdOut.println("Invalid number! Please enter numbers between 0 and 9");
//                        allValid = false;
//                        break;
//                    }
//                    if (rowSets[row].contains(num) || columnSets[col].contains(num) || boxSets[getBoxIndex(row, col)].contains(num)) {
//                        // FOR TESTING PURPOSES
//                        if (rowSets[row].contains(num)) {
//                            StdOut.format("Row: %d already contains num: %d\n", row + 1, num);
//                        }
//                        if (columnSets[col].contains(num)) {
//                            StdOut.format("Column: %d already contains num: %d\n", col + 1, num);
//                        }
//                        if (boxSets[getBoxIndex(row, col)].contains(num)) {
//                            StdOut.format("Box: %d already contains num: %d\n", getBoxIndex(row, col) + 1, num);
//                        }
//                        // TESTING DONE, COMMENT OUT AFTER TESTING
//                        StdOut.println("Duplicate number! Please enter only one 1-9 number in each row, column, and box");
//                        allValid = false;
//                        while(!backtrackStack.isEmpty()) {
//                            int[] index = backtrackStack.pop();
//                            deleteNumberFromSets(index[0], index[1]);
//                        }
//                        break;
//                    }
//                    // Int is valid, add to board
//                    sudokuBoard[row][col] = num;
//
//                    // Int is valid and not 0, add to sets
//                    if (num != 0) {
//                        rowSets[row].add(num);
//                        columnSets[col].add(num);
//                        boxSets[getBoxIndex(row, col)].add(num);
//                        backtrackStack.push(new int[] {row, col});
//                    }
//                } catch (NumberFormatException e) {
//                    StdOut.println("Invalid input! Please enter only integers");
//                    allValid = false;
//                    break;
//                }
//            }
//            // Moves on to the next iteration of the for loop if everything passes
//            if (allValid) {
//                isValid = true;
//
//                // This just prints the current board, before they add another row
//                // Not sure if I want to keep this
//                StdOut.println("Here's the current board: ");
//                for (int i = 0; i <= row; i++) {
//                    for (int j = 0; j < BOARD_SIZE; j++) {
//                        StdOut.format("%d ", sudokuBoard[i][j]);
//                    }
//                    StdOut.println();
//                }
//            }
//        }
//    }
//}


// Function that returns a stack filled with random indices through
//    private Stack<Integer> randomIndexStack() {
//        int[] flatIndices = new int[BOARD_SIZE * BOARD_SIZE];
//        int length = flatIndices.length;
//
//        // Fill the array with every index
//        for (int i = 0; i < length; i++) {
//            flatIndices[i] = i;
//        }
//
//        // Randomize the indices within the array
//        shuffle(flatIndices);
////        for (int i = 0; i < length; i++) {
////            StdOut.format("%d ", flatIndices[i]);
////        }
//        Stack<Integer> indexStack = new Stack<>();
//        Random rand = new Random();
//        for (int i = 0, j = length - 1; i < j; i++, j--) {
//            int randomNumber = rand.nextInt();
//            if (randomNumber % 2 == 0) {
//                indexStack.push(flatIndices[i]);
//                indexStack.push(flatIndices[j]);
//            }
//            else {
//                indexStack.push(flatIndices[j]);
//                indexStack.push(flatIndices[i]);
//            }
//            //StdOut.format("i = %d, j = %d\n", i, j);
//        }
//        if (length % 2 != 0) {
//            // length is odd, so we didn't add the middle index to the stack
//            int middle = length / 2;
//            //StdOut.format("Length is %d, middle is %d", length, middle);
//            indexStack.push(flatIndices[middle]);
//        }
//        //StdOut.format("Stack size = %d, array size = %d", indexStack.size(), length);
//        return indexStack;
//    }
//
//    /**
//     * Code from method java.util.Collections.shuffle();
//     */
//    private void shuffle(int[] array) {
//        Random random = new Random();
//        int count = array.length;
//        for (int i = count; i > 1; i--) {
//            swap(array, i - 1, random.nextInt(i));
//        }
//    }
//    private void swap(int[] array, int i, int j) {
//        int temp = array[i];
//        array[i] = array[j];
//        array[j] = temp;
//    }

// Generates a random unique final board
//    private void generateRandomSolution() {
//        int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
//
//        Stack<Integer> randomIndex = randomIndexStack();
//
//        int[] rowCol = getUnflattenIndex(randomIndex.pop());
//        int row;
//        int col;
//        int solutionCount;
//        int[][] startBoard;
//        int popCount = 0;
//
//        while (true) {
//            row = rowCol[0];
//            col = rowCol[1];
//            checkIfValidBoard(board);
//
//            int answer = findRandomAnswer(row, col);
//
//            if (answer > 0) {
//                // Add solution to board
//                board[row][col] = answer;
//                // Add solution to sets
//                rowSets[row].add(answer);
//                columnSets[col].add(answer);
//                boxSets[getBoxIndex(row, col)].add(answer);
//
//                startBoard = deepCopyBoard(board);
//                StdOut.println("Start board");
//                //printSudokuBoard(startBoard);
//                solutionCount = countUniqueSolutions(board);
//                StdOut.format("solutionCount: %d\n", solutionCount);
//                board = deepCopyBoard(startBoard);
//                if (solutionCount == 1) {
//                    break;
//                }
//            }
//            if (!randomIndex.isEmpty()) {
//                rowCol = getUnflattenIndex(randomIndex.pop());
//            }
//            else {
//                StdOut.println("Break");
//                break;
//            }
//        }
//        printSudokuBoard(board);
//    }
