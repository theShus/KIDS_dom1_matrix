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

        if (Objects.equals(multiplyTask.getNewName(), ""))
            newName = multiplyTask.getMatrixData1().getName() + multiplyTask.getMatrixData2().getName();
        else newName = multiplyTask.getNewName();

        App.multipliedMatrices.put(newName, new MatrixData(
                newName,
                result,
                multiplyTask.getMatrixData1().getRows(),
                multiplyTask.getMatrixData1().getCols(),
                "-"
        ));
    }

    //todo check
    private int[][] multiplyMatrices(MatrixData matrixData1, MatrixData matrixData2){
        int[][] result = new int[matrixData1.getRows()][matrixData2.getCols()];
        for (int i = 0; i < matrixData1.getRows(); i++) {
            for (int j = 0; j < matrixData2.getCols(); j++) {
                for (int k = 0; k < matrixData1.getCols(); k++) { // or matrixData2.getRows(), they are equal
                    result[i][j] += matrixData1.getMatrix()[i][k] * matrixData2.getMatrix()[k][j];
                }
            }
        }
        return result;
    }

    public void terminatePool(){
        threadPool.shutdown();
    }

}
