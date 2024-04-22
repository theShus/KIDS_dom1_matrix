package App.threadWorkers.pools;


import App.PropertyStorage;
import App.App;
import App.matrixData.MatrixData;
import App.matrixData.task.MultiplyTask;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatrixMultiplier {

    private final ExecutorService threadPool;
    private final ExecutorCompletionService<Map<String, Integer>> completionService;//todo change pool

    public MatrixMultiplier() {
        threadPool = Executors.newCachedThreadPool();
        this.completionService = new ExecutorCompletionService<>(threadPool);
    }

    public void multiplyMatricesAsync(MultiplyTask multiplyTask){

    }

    public void multiplyMatricesBlocking(MultiplyTask multiplyTask){
        String newName;
        int[][] result = multiplyMatrices(multiplyTask.getMatrixData1(), multiplyTask.getMatrixData2());

        if (Objects.equals(multiplyTask.getNewName(), ""))//ako nema custom ime samo cemo da spojimo imena matrica
            newName = multiplyTask.getMatrixData1().getName() + multiplyTask.getMatrixData2().getName();
        else newName = multiplyTask.getNewName();

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
        else if (matrixData1.getRows() > matrixData1.getCols()) {
            firstMatrix = matrixData2;
            secondMatrix = matrixData1;
        }

        int[][] mat1 = firstMatrix.getMatrix();
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
//        printMatrix(result);
        return result;
    }

    public static void printMatrix(int[][] matrix) {
        for (int[] ints : matrix) {
            for (int anInt : ints) {
                System.out.print(anInt + " ");
            }
            System.out.println(); // Move to the next line after printing each row
        }
    }

    public void terminatePool(){
        threadPool.shutdown();
    }

}
