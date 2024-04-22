package App.threadWorkers.pools;


import App.App;
import App.PropertyStorage;
import App.matrixData.MatrixData;
import App.matrixData.task.MultiplyTask;
import App.result.multiply.MultiplyResult;
import App.result.multiply.SubMultiplyResult;
import App.threadWorkers.pools.workers.MatrixMultiplicationWorker;

import java.util.*;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MatrixMultiplier {

    private final ExecutorService threadPool;
    private final ExecutorCompletionService<SubMultiplyResult> completionService;//todo change pool
//    private static final int MAXIMUM_ROWS_SIZE = PropertyStorage.getInstance().getMaximum_rows_size();
    private static final int MAXIMUM_ROWS_SIZE = 2;

    public MatrixMultiplier() {
        threadPool = Executors.newCachedThreadPool();
        this.completionService = new ExecutorCompletionService<>(threadPool);
    }

    public void multiplyMatricesAsync(MultiplyTask multiplyTask){
        //ako nema custom ime samo cemo da spojimo imena matrica
        String newName;
        if (Objects.equals(multiplyTask.getNewName(), "")){
            newName = multiplyTask.getMatrixData1().getName() + multiplyTask.getMatrixData2().getName();
        } else newName = multiplyTask.getNewName();


        //orjentisati matrice pravilno
        MatrixData firstMatrix = multiplyTask.getMatrixData1();
        MatrixData secondMatrix = multiplyTask.getMatrixData2();
        if (firstMatrix.getCols() != secondMatrix.getRows() || secondMatrix.getCols() != firstMatrix.getRows()) {
            System.err.println("Matrices can not be multiplied (row/col dont match)");
        }
        else if (firstMatrix.getRows() > firstMatrix.getCols()) {//okrecemo jer hocemo da matrica sa manje redova bude levo (tako podeseni for-ovi ispod)
            firstMatrix = multiplyTask.getMatrixData2();
            secondMatrix = multiplyTask.getMatrixData1();
        }

        //podeliti matrice na redove i kolone za workere
        List<int[][]> subMatricesA = extractRowSubMatrices(firstMatrix.getMatrix());
        List<int[][]> subMatricesB = extractColumnSubMatrices(secondMatrix.getMatrix());
        if (subMatricesA.size() != subMatricesB.size()){
            System.err.println("Matrices have malformed while splitting, exiting");
            return;
        }

        //posalje se podeljeno u workere
        List<Future<SubMultiplyResult>> matrixMultiplyResults = new ArrayList<>();
        for (int i = 0; i < subMatricesA.size(); i++) {
            for (int j = 0; j < subMatricesB.size(); j++) {
                matrixMultiplyResults.add(this.completionService.submit(
                        new MatrixMultiplicationWorker(i * MAXIMUM_ROWS_SIZE, j * MAXIMUM_ROWS_SIZE, subMatricesA.get(i), subMatricesB.get(j))));
            }
        }

        //stavi se future na queue
        int finalRows = firstMatrix.getRows();
        int finalCols = secondMatrix.getCols();
        App.resultQueue.add(new MultiplyResult(newName, matrixMultiplyResults, finalRows, finalCols));
    }


    public static List<int[][]> extractRowSubMatrices(int[][] matrix) {
        List<int[][]> submatrices = new ArrayList<>();
        int rows = matrix.length;
        int cols = matrix[0].length;

        for (int i = 0; i < rows; i += MAXIMUM_ROWS_SIZE) {
            int rowCount = Math.min(MAXIMUM_ROWS_SIZE, rows - i);  // Handle cases where rows aren't multiple of 3
            int[][] submatrix = new int[rowCount][cols];
            for (int r = 0; r < rowCount; r++) {
                System.arraycopy(matrix[i + r], 0, submatrix[r], 0, cols);
            }
            submatrices.add(submatrix);
        }
        return submatrices;
    }

    public static List<int[][]> extractColumnSubMatrices(int[][] matrix) {
        List<int[][]> submatrices = new ArrayList<>();
        int rows = matrix.length;
        int cols = matrix[0].length;

        for (int j = 0; j < cols; j += MAXIMUM_ROWS_SIZE) {
            int colCount = Math.min(MAXIMUM_ROWS_SIZE, cols - j);  // Handle cases where columns aren't multiple of 3
            int[][] submatrix = new int[rows][colCount];
            for (int r = 0; r < rows; r++) {
                System.arraycopy(matrix[r], j, submatrix[r], 0, colCount);
            }
            submatrices.add(submatrix);
        }
        return submatrices;
    }



    //BLOCKING

    public void multiplyMatricesBlocking(MultiplyTask multiplyTask){
        String newName;
        if (Objects.equals(multiplyTask.getNewName(), "")){ //ako nema custom ime samo cemo da spojimo imena matrica
            newName = multiplyTask.getMatrixData1().getName() + multiplyTask.getMatrixData2().getName();
        } else newName = multiplyTask.getNewName();

        int[][] result = multiplyMatrices(multiplyTask.getMatrixData1(), multiplyTask.getMatrixData2());
        App.cashedMatrices.put(newName, new MatrixData(
                newName,
                result,
                multiplyTask.getMatrixData1().getRows(),
                multiplyTask.getMatrixData1().getCols(),
                "-"
        ));
    }

    private int[][] multiplyMatrices(MatrixData matrixData1, MatrixData matrixData2) {
        MatrixData firstMatrix = matrixData1;
        MatrixData secondMatrix = matrixData2;

        //Orjentisemo matrice tako da se poklope za mnozenje
        if (matrixData1.getCols() != matrixData2.getRows() || matrixData2.getCols() != matrixData1.getRows()) {
            System.err.println("Matrices can not be multiplied (row/col dont match)");
            return null;
        }
        else if (matrixData1.getRows() > matrixData1.getCols()) {//okrecemo jer hocemo da matrica sa manje redova bude levo (tako podeseni for-ovi ispod)
            firstMatrix = matrixData2;
            secondMatrix = matrixData1;
        }

        int[][] mat1 = firstMatrix.getMatrix();//da ne bi get-ovali konstantno, malo efikasnije
        int[][] mat2 = secondMatrix.getMatrix();

        //Mnozimo matrice
        int[][] result = new int[firstMatrix.getRows()][secondMatrix.getCols()];
        for (int i = 0; i < firstMatrix.getRows(); i++) {
            for (int j = 0; j < secondMatrix.getCols(); j++) {
                for (int k = 0; k < firstMatrix.getCols(); k++) {
                    result[i][j] += mat1[i][k] * mat2[k][j];
                }
            }
        }
        return result;
    }

    public void terminatePool(){
        threadPool.shutdown();
    }

}
