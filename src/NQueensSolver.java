/*
 * Formulates the N-Queens Puzzle as a 0-1 Integer Linear Program and solves it
 * using the SCPSolver + GLPK tools.
 * Copyright (C) 2015 Russell Andrew Edson
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import scpsolver.constraints.LinearEqualsConstraint;
import scpsolver.constraints.LinearSmallerThanEqualsConstraint;
import scpsolver.lpsolver.LinearProgramSolver;
import scpsolver.lpsolver.SolverFactory;
import scpsolver.problems.LinearProgram;

/**
 * A program to take in a given positive integer N > 3, and return a solution
 * for the N-Queens Puzzle.
 */
public class NQueensSolver {
    /**
     * Adds the "one queen per row" constraints to the linear program,
     *   ie. the sum from j=1 to n of x_i,j = 1 for all i=1,...,n.
     */
    public static void makeQueenRowConstraints(int n, LinearProgram lp) {
        double[][] rowConstraintsMatrix = new double[n][n*n];
        for (int row = 0; row < n; row++) {
            for (int column = n*row; column < n*row + n; column++) {
                rowConstraintsMatrix[row][column] = 1;
            }
        }

        for (double[] row : rowConstraintsMatrix) {
            lp.addConstraint(new LinearEqualsConstraint(row, 1, ""));
        }

        //printConstraintsMatrix(rowConstraintsMatrix);
        //System.out.println();
    }

    /**
     * Adds the "one queen per column" constraints to the linear program,
     *   ie. the sum from i=1 to n of x_i,j = 1 for all j=1,...,n.
     */
    public static void makeQueenColumnConstraints(int n, LinearProgram lp) {
        double[][] columnConstraintsMatrix = new double[n][n*n];
        for (int row = 0; row < n; row++) {
            for (int column = row; column < n*n; column += n) {
                columnConstraintsMatrix[row][column] = 1;
            }
        }

        for (double[] row : columnConstraintsMatrix) {
            lp.addConstraint(new LinearEqualsConstraint(row, 1, ""));
        }

        //printConstraintsMatrix(columnConstraintsMatrix);
        //System.out.println();
    }

    /**
     * Adds the "at most one queen per diagonal across the board"
     * constraints to the linear program.
     */
    public static void makeQueenDiagonalConstraints(int n, LinearProgram lp) {

        /* Here we create the constraints for the "forward diagonals" where
         * we move along the first row.
         *   ie. x_1,j + the sum of x_1+m,j+m is <= 1 for all j=1,...,n.
         */
        double[][] rowForwardConstraintsMatrix = new double[n][n*n];
        for (int row = 0; row < n; row++) {
            for (int column = row; column < n*n - row*n; column += (n+1)) {
                rowForwardConstraintsMatrix[row][column] = 1;
            }
        }

        for (double[] row : rowForwardConstraintsMatrix) {
            lp.addConstraint(new LinearSmallerThanEqualsConstraint(row, 1, ""));
        }

        //printConstraintsMatrix(rowForwardConstraintsMatrix);
        //System.out.println();

        /* Here we create the constraints for the "forward diagonals" where
         * we move along the first column.
         *   ie. x_i,1 + the sum of x_i+m,1+m <= 1 for all i=1,...,n.
         */
        double[][] columnForwardConstraintsMatrix = new double[n][n*n];
        for (int row = 0; row < n; row++) {
            for (int column = n*row; column < n*n; column += (n+1)) {
                columnForwardConstraintsMatrix[row][column] = 1;
            }
        }

        for (double[] row : columnForwardConstraintsMatrix) {
            lp.addConstraint(new LinearSmallerThanEqualsConstraint(row, 1, ""));
        }

        //printConstraintsMatrix(columnForwardConstraintsMatrix);
        //System.out.println();

        /* Here we create the constraints for the "backward diagonals" where
         * we move along the first row.
         *   ie. x_1,j + the sum of x_1+m,j-m <= 1 for all j=1,...,n.
         */
        double[][] rowBackwardConstraintsMatrix = new double[n][n*n];
        for (int row = 0; row < n; row++) {
            for (int column = row; column < n*row + 1; column += (n-1)) {
                rowBackwardConstraintsMatrix[row][column] = 1;
            }
        }

        for (double[] row : rowBackwardConstraintsMatrix) {
            lp.addConstraint(new LinearSmallerThanEqualsConstraint(row, 1, ""));
        }

        //printConstraintsMatrix(rowBackwardConstraintsMatrix);
        //System.out.println();

        /* Here we create the constraints for the "backward diagonals" where
         * we move along the last column.
         *   ie. x_i,n + the sum of x_i+m,n-m <= 1 for all i=1,...,n.
         */
        double[][] columnBackwardConstraintsMatrix = new double[n][n*n];
        for (int row = 0; row < n; row++) {
            for (int column = n*(row+1) - 1; column < n*n; column += (n-1)) {
                columnBackwardConstraintsMatrix[row][column] = 1;
            }
        }

        /* Note: Our iteration here always gives us an extra '1' at the end of
         * the top row, which we can simply remove here.
         */
        columnBackwardConstraintsMatrix[0][n*n - 1] = 0;

        for (double[] row : columnBackwardConstraintsMatrix) {
            lp.addConstraint(new LinearSmallerThanEqualsConstraint(row, 1, ""));
        }

        //printConstraintsMatrix(columnBackwardConstraintsMatrix);
        //System.out.println();
    }

    /**
     * A helper method we can use to check the constraints matrices for
     * debugging purposes.
     */
    public static void printConstraintsMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print((int) matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);

        /* Our objective function simply sums up all the x_i,j. */
        double[] objectiveFunction = new double[n*n];
        for (int i = 0; i < n*n; i++) {
            objectiveFunction[i] = 1;
        }

        LinearProgram queens = new LinearProgram(objectiveFunction);
        queens.setMinProblem(false);

        /* All of the x_i,j variables are binary (0-1). */
        for (int i = 0; i < n*n; i++) {
            queens.setBinary(i);
        }

        makeQueenRowConstraints(n, queens);
        makeQueenColumnConstraints(n, queens);
        makeQueenDiagonalConstraints(n, queens);

        LinearProgramSolver solver = SolverFactory.newDefault();
        double[] solution = solver.solve(queens);

        /* We print out the solution after we find it -- this tells us where
         * we need to place the queens!
         */
        System.out.println();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print("x"+ (i+1) + "," + (j+1) +
                        "=" + (int) solution[n*i+j] + "  ");
            }
            System.out.println();
        }
    }
}
