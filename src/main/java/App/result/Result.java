package App.result;

import App.matrixData.task.TaskType;

public interface Result {

    TaskType getScanType();



    int[][] getResult();

    boolean futureIsDone();
}
