package App.threadWorkers;

import App.App;
import App.matrixData.MatrixData;
import App.matrixData.task.TaskType;
import App.result.MultiplyResult;
import App.result.Result;
import App.result.ScanResult;

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
                    }
                    else App.resultQueue.add(result);
                }
                else if (result.getScanType() == TaskType.MULTIPLY) {
                    MultiplyResult scanResult = (MultiplyResult) result;

//                    App.logger.resultRetrieverSorter("Matrix " + scanResult.getMatrixName() + " is finished scanning, adding to cache");
                    //todo
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
