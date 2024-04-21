package App.matrixDat.task;

import App.matrixDat.MatrixData;

public class ScanTask implements Task{

    private final String filePath;

    public ScanTask(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.CREATE;
    }

    public String getFilePath() {
        return filePath;
    }

}
