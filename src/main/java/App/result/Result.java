package App.result;

import App.matrixDat.task.TaskType;

import java.util.Map;

public interface Result {

    TaskType getScanType();



    int[][] getResult();

    boolean futureIsDone();
}
