package App.matrixDat.task;

import App.matrixDat.MatrixData;

public class MultiplyTask implements Task{

    private MatrixData matrixData1;
    private MatrixData matrixData2;



    @Override
    public TaskType getTaskType() {
        return TaskType.MULTIPLY;
    }

    public void setMatrix1(MatrixData matrixData1) {
        this.matrixData1 = matrixData1;
    }

    public void setMatrix2(MatrixData matrixData2) {
        this.matrixData2 = matrixData2;
    }

    public MatrixData getMatrix1() {
        return matrixData1;
    }

    public MatrixData getMatrix2() {
        return matrixData2;
    }

}
