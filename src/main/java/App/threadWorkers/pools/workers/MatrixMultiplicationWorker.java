package App.threadWorkers.pools.workers;

import App.result.multiply.SubMultiplyResult;

import java.util.concurrent.Callable;

public class MatrixMultiplicationWorker implements Callable<SubMultiplyResult> {

    private final int cordX;
    private final int cordY;
    private final int[][] mat1;
    private final int[][] mat2;

    public MatrixMultiplicationWorker(int cordX, int cordY, int[][] mat1, int[][] mat2) {
        this.cordX = cordX;
        this.cordY = cordY;
        this.mat1 = mat1;
        this.mat2 = mat2;
    }

    @Override
    public SubMultiplyResult call() throws Exception {
        int mat1Rows = mat1.length;
        int mat2Cols = mat2[0].length;

        int[][] result = new int[mat1Rows][mat2Cols];
        for (int i = 0; i < mat1Rows; i++) {
            for (int j = 0; j < mat2Cols; j++) {
                for (int k = 0; k < mat2Cols; k++) {
                    result[i][j] += mat1[i][k] * mat2[k][j];
                }
            }
        }

        return new SubMultiplyResult(cordX, cordY, result);
    }

}
