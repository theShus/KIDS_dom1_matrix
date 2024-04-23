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
//            return;
        }
        else if (firstMatrix.getRows() > firstMatrix.getCols()) {//okrecemo jer hocemo da matrica sa manje redova bude levo (tako podeseni for-ovi ispod)
            firstMatrix = multiplyTask.getMatrixData2();
            secondMatrix = multiplyTask.getMatrixData1();
        }

//        podeliti matrice na redove i kolone za workere
        List<int[]> subMatricesARow = extractRowsAsArrays(firstMatrix.getMatrix());
        List<int[]> subMatricesBColumns = extractColumnsAsArrays(secondMatrix.getMatrix());


//        System.out.println("ROWW");
//        for (int[] a: subMatricesARow) {
//            System.out.println("---");
//            System.out.println(Arrays.toString(a));
//        }
//        System.out.println("Columns");
//        for (int[] b: subMatricesBColumns) {
//            System.out.println("---");
//            System.out.println(Arrays.toString(b));
//        }

        if (subMatricesARow.size() != subMatricesBColumns.size()){
            System.err.println("Matrices have malformed while splitting, exiting");
            return;
        }
        //posalje se podeljeno u workere
        List<Future<SubMultiplyResult>> matrixMultiplyResults = new ArrayList<>();

        for (int rowCounter = 0; rowCounter < subMatricesARow.size(); rowCounter += MAXIMUM_ROWS_SIZE) {
            for (int colCounter = 0; colCounter < subMatricesBColumns.size(); colCounter += MAXIMUM_ROWS_SIZE) {

                List<int[]> rowsForWorker = new ArrayList<>();
                List<int[]> colsForWorker = new ArrayList<>();

                for (int i = 0; i < MAXIMUM_ROWS_SIZE; i++) {
                    rowsForWorker.add(subMatricesARow.get(rowCounter + i));
                    colsForWorker.add(subMatricesBColumns.get(colCounter + i));
                }

                matrixMultiplyResults.add(this.completionService.submit(new MatrixMultiplicationWorker(rowCounter , colCounter , rowsForWorker, colsForWorker)));

            }
        }

//        stavi se future na queue
        int finalRows = firstMatrix.getRows();
        int finalCols = secondMatrix.getCols();
        App.resultQueue.add(new MultiplyResult(newName, matrixMultiplyResults, finalRows, finalCols));
    }

    public List<int[]> extractRowsAsArrays(int[][] matrix) {
        List<int[]> rowList = new ArrayList<>();
        for (int[] row : matrix) {
            // Clone the row to ensure that changes to the original matrix do not affect the extracted rows
            int[] clonedRow = row.clone();
            rowList.add(clonedRow);
        }
        return rowList;
    }

    public static List<int[]> extractColumnsAsArrays(int[][] matrix) {
        List<int[]> columnList = new ArrayList<>();
        int rows = matrix.length;

        int cols = matrix[0].length;

        // Iterate over each column in the matrix
        for (int j = 0; j < cols; j++) {
            int[] column = new int[rows];  // Create a new array for the column
            for (int i = 0; i < rows; i++) {
                column[i] = matrix[i][j];  // Copy each row's j-th element into the column array
            }
            columnList.add(column);  // Add the column array to the list
        }
        return columnList;
    }

    public static void printMatrix(int[][] matrix) {
        for (int[] ints : matrix) {
            for (int anInt : ints) {
                System.out.print(anInt + " ");
            }
            System.out.println(); // Move to the next line after printing each row
        }
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
