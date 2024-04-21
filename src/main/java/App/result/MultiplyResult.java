package App.result;

import App.matrixDat.task.TaskType;

import java.util.Map;

public class MultiplyResult implements Result{
    @Override
    public TaskType getScanType() {
        return TaskType.MULTIPLY;
    }

    @Override
    public int[][]getResult() {
        return null;
    }

    @Override
    public boolean futureIsDone() {
        return false;
    }
}
