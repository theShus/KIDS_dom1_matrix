package App.threadWorkers;

import App.App;
import App.matrixData.MatrixData;
import App.matrixData.task.SquareTask;
import App.matrixData.task.TaskType;
import App.result.multiply.MultiplyResult;
import App.result.Result;
import App.result.scan.ScanResult;
import App.result.scan.SquareResult;

public class MatrixBrain extends Thread{

    private boolean running = true;

    @Override
    public void run() {
        while (running){
            try {
                Result result = App.resultQueue.take();

                if (result.getScanType() == TaskType.CREATE) {
                    ScanResult scanResult = (ScanResult) result;
                    if (scanResult.futureIsDone()){
                        App.logger.resultRetrieverSorter("Matrix " + scanResult.getMatrixName() + " is finished scanning, adding to cache");
                        MatrixData matrixData = new MatrixData(scanResult.getMatrixName(), scanResult.getResult(), scanResult.getRows(), scanResult.getCols(), scanResult.getFilePath());
                        App.cashedMatrices.put(scanResult.getMatrixName(), matrixData);

                        App.taskQueue.add(new SquareTask(scanResult.getMatrixName()));//stavimo novi task da se napravi kvadratna matrica
                    }
                    else App.resultQueue.add(result);
                }
                else if (result.getScanType() == TaskType.SQUARE) {//todo check
                    MatrixData squaredMatrixData = ((SquareResult) result).getResult();
                    App.logger.resultRetrieverSorter("Matrix " + squaredMatrixData.getName() + " has been squared, adding to cache");
                    App.cashedMatrices.put(squaredMatrixData.getName(), squaredMatrixData);
                }
                else if (result.getScanType() == TaskType.MULTIPLY) {
                    MultiplyResult multiplyResult = (MultiplyResult) result;
                    if (multiplyResult.futureIsDone()){
                        App.logger.resultRetrieverSorter("Matrix " + multiplyResult.getMatrixName() + " is finished scanning, adding to cache");
                        App.cashedMatrices.put(multiplyResult.getMatrixName(),
                                new MatrixData(multiplyResult.getMatrixName(), multiplyResult.getResult(), multiplyResult.getRows(), multiplyResult.getCols(), "-"));

//                        printMatrix(multiplyResult.getResult());
                    }
                    else App.resultQueue.add(result);
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void terminate() {
        System.err.println("Terminating MatrixBrain thread");
        running = false;
    }
}
