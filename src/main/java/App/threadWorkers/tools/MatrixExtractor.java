package App.threadWorkers.tools;

import App.PropertyStorage;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class MatrixExtractor {


    /*
     delimo segmente po max chung size time sto postavimo granice prvo
     ako se granica ne nalazi na \n (kraju linije) idemo unazad (da ne bi bilo vise od max size) dok ne dodjemo do kraja reda
     zatim to napravimo u segment
     */
    public SplitMatrix calculateSegments(String filePath) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filePath, "r");

        String firstLine = file.readLine(); // Uzmemo podatke iz prve linije
        String[] metadata = firstLine.split(", ");
        String matrixName = metadata[0].split("=")[1];
        int rows = Integer.parseInt(metadata[1].split("=")[1]);
        int cols = Integer.parseInt(metadata[2].split("=")[1]);

        List<long[]> segments = new ArrayList<>();
        long fileSize = file.length();

        long currentOffset = 0;
        while (file.read() != '\n') {//nadjemo prvu liniju matrice (pointer se pomerio kad smo gledali metadata, prvu liniju)
            file.seek(++currentOffset);
        }

        while (currentOffset < fileSize) {//idi dok ne dodjes do kraja
            long segmentStart = currentOffset;
            long segmentEnd = segmentStart + PropertyStorage.getInstance().getMaximum_file_chunk_size();//povecavaj za chunk size

            if (segmentEnd > fileSize) {//ako je chunk veci od file, uzemi file size
                segmentEnd = fileSize;
            }
            else {
                file.seek(segmentEnd);
                while (segmentEnd > segmentStart && file.read() != '\n') {//nadji kraj linije tako da ne uzmes u sredini
                    segmentEnd--;
                    file.seek(segmentEnd);
                }
            }

            segments.add(new long[]{segmentStart, segmentEnd});//dodaj granice segmenta
            currentOffset = segmentEnd ;
        }

        file.close();
        return new SplitMatrix(matrixName, segments, rows, cols);//vracamo u klasi kako bi lakse vradili metadata
    }

}
