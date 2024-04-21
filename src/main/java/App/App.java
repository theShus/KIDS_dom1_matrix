package App;

import App.logger.Logger;
import App.matrixDat.MatrixData;
import App.matrixDat.task.ScanTask;
import App.matrixDat.task.Task;
import App.result.Result;
import App.threadWorkers.MatrixBrain;
import App.threadWorkers.SystemExplorer;
import App.threadWorkers.TaskCoordinator;
import App.threadWorkers.tools.MatrixExtractor;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class App {


    //Queue
    public static final BlockingQueue<Task> taskQueue = new LinkedBlockingQueue<>(20);
    public static final BlockingQueue<Result> resultQueue = new LinkedBlockingQueue<>(100);

    //Results
    public static final Map<String, MatrixData> scannedMatrices = new ConcurrentHashMap<>();
    public static final Map<String, MatrixData> multipliedMatrices = new ConcurrentHashMap<>();

    //Else
    private static final CopyOnWriteArrayList<String> dirsToExplore = new CopyOnWriteArrayList<>();
    public static final Logger logger = new Logger();
    public static final MatrixExtractor matrixExtractor = new MatrixExtractor();

    //Threads
    private static final SystemExplorer systemExplorer = new SystemExplorer(dirsToExplore);
    private static final TaskCoordinator taskCoordinator = new TaskCoordinator();
    private static final MatrixBrain matrixBrain = new MatrixBrain();
    //todo matrix multiplier

    public void start() {
        PropertyStorage.getInstance().loadProperties();
        dirsToExplore.add(PropertyStorage.getInstance().getStart_dir());

        systemExplorer.start(); //todo remove this below
//        taskQueue.add(new ScanTask("C:\\Users\\Shus\\Programming\\IdeaProjects\\KIDS_matrix\\KIDS_matrix\\src\\main\\resources\\matrix_data\\c3_file.rix"));
        taskCoordinator.start();
        matrixBrain.start();

        startCommandParser();
    }

    private void startCommandParser() {
        Scanner cli = new Scanner(System.in);
        String line;
        String[] tokens;

        while (true) {
            line = cli.nextLine().trim();
            tokens = line.split(" ");

            if (line.isEmpty()) continue;

            switch (tokens[0]) {//starter command
                case "dir" -> {
                    System.out.println("dir");
                    if (badCommandLength(tokens.length, 2)) continue;

                    //todo  dirsToCrawl.add(tokens[1]);
                    logger.cli("Added directory to scan list");
                }
                case "info" -> {
                    System.out.println("info");


                    System.out.println(scannedMatrices.get("C3").getMatrix()[0][0]);

//                    printMatrix(scannedMatrices.get("C3").getMatrix());



//                    if (badCommandLength(tokens.length, 2)) continue;

//                    if (tokens[1].equals("-all")) {
//                        System.out.println("all");
//                        //todo
//                    } else if (tokens[1].equals("-asc")) {
//                        System.out.println("asc");
//                        //todo
//                    } else if (tokens[1].equals("-desc")) {
//                        System.out.println("desc");
//                        //todo
//                    } else if (tokens[1].equals("-s")) {
//                        System.out.println("s");
//                        //todo
//                    } else if (tokens[1].equals("-e")) {
//                        System.out.println("e");
//                        //todo
//                    }
                }

                case "multiply" -> {
                    System.out.println("multiply");
                    if (badCommandLength(tokens.length, 3)) continue;


                    if (tokens[1].equals("-async")) {
                        System.out.println("async");
                        //todo
                    } else if (tokens[1].equals("-name")) {
                        System.out.println("name");
                        //todo
                    }
                }
                case "save" -> {
                    System.out.println("save");
                    if (badCommandLength(tokens.length, 2)) continue;

                }
                case "clear" -> {
                    System.out.println("clear");
                    if (badCommandLength(tokens.length, 2)) continue;

                }
                case "help" -> logger.cli
                        (
                                """
                                        --> dir <dir_name>:  Dodaje novi direktorijum za skeniranje
                                        --> info <matrix_name>:  Dohvata osnovne informacije o specifičnoj matrici ili skupu matrica\040
                                          ->  -all: Prikazuje sve matrice (koristi se bez arguments za naziv matrice).
                                          ->  -asc: Sortira matrice rastuće po broju redova, a zatim po broju kolona.
                                          ->  -desc: Sortira matrice opadajuće po broju redova, a zatim po broju kolona.
                                          ->  -s <n>: Prikazuje prvih N matrica. Na primer, -s 10 prikazuje prvih 10 matrica.
                                          ->  -e <n>: Prikazuje poslednjih N matrica. Na primer, -e 5 prikazuje poslednjih 5 matrica.
                                        --> multiply <mat1,mat2>:  Korisnik  zatraži množenje dve matrice\040
                                          ->    -async: Omogućava asinhrono izvršavanje množenja, bez blokiranja Matrix Brain niti.
                                          ->    -name <matrix_name>: Omogućava imenovanje matrice, ukoliko se ne navede kao parametar ime koje matrica koristi je konkatenacija naziva prve i druge matrice, primer: mat1mat2
                                        --> save -name <mat_name> -file <file_name>:  Omogućava korisniku da sačuva matricu na disk
                                        --> clear <mat_name> : Brisu se rezultati zeljene matrice
                                        --> clear <file_name>: Brisu se sve matrice sa upisanog fajla i kupe se opet (automatski)
                                        --> stop : Gasi aplikaciju
                                        """
                        );
                case "stop" -> {
                    logger.cli("Stopping the app");
                    stopThreads();
                    cli.close();
                    return;
                }
                default -> System.err.println("Unknown command");
            }
        }
    }


    public static void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println(); // Move to the next line after printing each row
        }
    }

    private boolean badCommandLength(int tokenLength, int wantedSize) {
        if (tokenLength > wantedSize - 1) {
            System.err.println("Too many argument");
            return true;
        }
        return false;
    }

    private void stopThreads() {
        System.err.println("STOPPING THREADS");

        systemExplorer.terminate();
        taskCoordinator.terminate();
        matrixBrain.terminate();
    }

}
