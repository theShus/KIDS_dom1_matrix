package App.threadWorkers;

import App.App;
import App.matrixDat.task.MultiplyTask;
import App.matrixDat.task.ScanTask;
import App.matrixDat.task.Task;
import App.matrixDat.task.TaskType;
import App.result.ScanResult;
import App.threadWorkers.tools.MatrixExtractor;
import App.threadWorkers.tools.SplitMatrix;
import App.threadWorkers.workers.MatrixScanWorker;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TaskCoordinator extends Thread {

    private boolean running = true;



    @Override
    public void run() {
        while (running) {//uzima poslove sa TaskQueue i rasporedjuje ih u Scan ili Multiply queue
            try {
                Task task = App.taskQueue.take();

                if (task instanceof ScanTask) {
                    String filePath = ((ScanTask) task).getFilePath(); //todo vrati ovo nazad
//                    String filePath = "C:\\Users\\Shus\\Programming\\IdeaProjects\\KIDS_matrix\\KIDS_matrix\\src\\main\\resources\\matrix_data\\c3_file.rix";
                    App.logger.jobDispatcher("Submitted matrix for scanning: " + filePath);
                    App.matrixExtractor.sendMatrixForScanning(filePath);
                }
                else if (task.getTaskType() == TaskType.MULTIPLY) {
                    App.logger.jobDispatcher("Submitted matrices for multiplication: " + ((MultiplyTask) task).getMatrix1().getName() + " * " + ((MultiplyTask) task).getMatrix2().getName());

                    //todo pool submit
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void terminate() {
        System.err.println("Terminating TaskCoordinator thread");
        running = false;
    }

}
