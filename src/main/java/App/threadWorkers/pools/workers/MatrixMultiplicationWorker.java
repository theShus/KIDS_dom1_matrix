package App.threadWorkers.pools.workers;

import App.result.multiply.SubMultiplyResult;

import java.util.List;
import java.util.concurrent.Callable;


public class MatrixMultiplicationWorker implements Callable<SubMultiplyResult> {

    private final int cordX;
    private final int cordY;
    private final List<int[]> rows;
    private final List<int[]> cols;

    public MatrixMultiplicationWorker(int cordX, int cordY, List<int[]> rows, List<int[]> cols) {
        this.cordX = cordX;
        this.cordY = cordY;
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public SubMultiplyResult call() throws Exception {
        int numRows = rows.size();
        int numCols = cols.size();


        int[][] resultMatrix = new int[numRows][numCols];

        // Iterate through each row
        for (int i = 0; i < numRows; i++) {
            int[] currentRow = rows.get(i);

            // Iterate through each column
            for (int j = 0; j < numCols; j++) {
                int[] currentCol = cols.get(j);
                int sum = 0;

                // Multiply corresponding elements and add them
                for (int k = 0; k < currentRow.length; k++) {
                    sum += currentRow[k] * currentCol[k];
                }

                // Store the result in the matrix
                resultMatrix[i][j] = sum;
            }
        }
//        printMatrix(resultMatrix);

        return new SubMultiplyResult(cordX, cordY, resultMatrix);
    }

    public static void printMatrix(int[][] matrix) {
        System.out.println("------");

        for (int[] ints : matrix) {
            for (int anInt : ints) {
                System.out.print(anInt + " ");
            }
            System.out.println(); // Move to the next line after printing each row
        }
    }
}
