package App.result;

import App.matrixData.task.TaskType;

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
