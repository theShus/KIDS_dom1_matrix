package App.matrixDat;

public class MatrixData {
    private String name;
    private int[][] matrix;
    private int rows;
    private int cols;

    public MatrixData(String name, int[][] matrix, int rows, int cols) {
        this.name = name;
        this.matrix = matrix;
        this.rows = rows;
        this.cols = cols;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(int[][] matrix) {
        this.matrix = matrix;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }
}