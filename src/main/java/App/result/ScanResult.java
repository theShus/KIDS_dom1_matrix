package App.result;

import App.matrixDat.task.TaskType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ScanResult implements Result{


    private final String matrixName;
    private final List<Future<Map<String, Integer>>> futureResults;
    private final int rows;
    private final int cols;

    public ScanResult(String matrixName, List<Future<Map<String, Integer>>> futureResults, int rows, int cols) {
        this.matrixName = matrixName;
        this.futureResults = futureResults;
        this.rows = rows;
        this.cols = cols;
    }


    public String getMatrixName() {
        return matrixName;
    }

    @Override
    public TaskType getScanType() {
        return TaskType.CREATE;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    @Override
    public int[][] getResult() {
        //prodjemo kroz listu rezultata (lista jer podeljena matrica u segmente)
        //svaki element liste je mapa, prodjemo kroz njih i stavimo sve vrednosti u jednu totalnu matricu
        try{
            int[][] matrix = new int[rows][cols];

            for (Future<Map<String, Integer>> future : futureResults) {
                Map<String, Integer> resultMap = future.get();
                for (Map.Entry<String, Integer> entry : resultMap.entrySet()) {
                    String[] parts = entry.getKey().split(",");
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);
                    matrix[row][col] = entry.getValue();
                }
            }
            return matrix;
        }
        catch (Exception e){
           e.printStackTrace();
        }
        return null;
    }



    @Override
    public boolean futureIsDone() {
        for (Future<Map<String, Integer>> future : futureResults) {
            if (!future.isDone()) {
                return false;
            }
        }
        return true;
    }

}
