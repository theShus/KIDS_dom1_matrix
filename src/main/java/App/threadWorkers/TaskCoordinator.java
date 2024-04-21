package App.threadWorkers;

import App.App;
import App.matrixData.task.MultiplyTask;
import App.matrixData.task.ScanTask;
import App.matrixData.task.Task;
import App.matrixData.task.TaskType;

import java.io.IOException;

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
                    App.logger.jobDispatcher("Submitted matrices for multiplication: " + ((MultiplyTask) task).getMatrixData1().getName() + " * " + ((MultiplyTask) task).getMatrixData2().getName());

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
