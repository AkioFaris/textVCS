import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created by Akio on 10/10/2015.
 */
public class VCS {
  private static final String LINE_SEPARATOR = "\n";


  public static void createVCFile(final InputStream inStream, final OutputStream outStream, final String revision) throws IOException {
    try (
            final BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
            final OutputStreamWriter outW = new OutputStreamWriter(outStream);
            final PipedOutputStream pout = new PipedOutputStream();
            final PipedInputStream pin = new PipedInputStream(pout)
    ) {
      final byte[] buffer = new byte[1024];
      while (inStream.read(buffer) > -1) {
        pout.write(buffer);
       // System.out.println(buffer.toString()); //tmp
      }
     // System.out.println("I'm here! (1)"); //tmp
      final String versionHeader = "r" + revision + LINE_SEPARATOR// + Objects.toString(getNumberOfLines(pin))
              + " operations";
      outW.write(versionHeader);
      //System.out.println("I'm here! (2)"); //tmp

      String line;
      while ((line = br.readLine()) != null) {
        outW.write(LINE_SEPARATOR + line);
      //  System.out.println(LINE_SEPARATOR + line); //tmp
      }
      //outW.flush();
    //  System.out.println("I'm here! (3)"); //tmp

    }
  }

  public static void update(final InputStream inStream, final OutputStream outStream, final String revision) throws IOException {
    String prevRevision = null;
    //final String currRevision;
    final Integer[] lineLCSIndex = null;
    final int LCSLen;
    try (
            final BufferedReader br = new BufferedReader(new InputStreamReader(inStream))) {/*get last revision message*/
      String line;
      while ((line = br.readLine()) != null) {
        if (line.startsWith("r")) {
          prevRevision = line.substring(1);
        }
      }

//      /*get temporary file (last revision version)*/
//      final File tempFile = new File(inFile.getAbsolutePath() + ".prevVersion");
//      getFileVersion(sourceFilename, tempFile.getPath(), prevRevision);
//      if (!tempFile.exists()) {
//        if (!tempFile.createNewFile()) {
//          throw new IOException("Unable to create tmp file: " + tempFile.getPath());
//        }
//      }
//
//      ArrayList<Integer> commonLines = LCS(inFile, tempFile);
//
//      if (!tempFile.delete()) {
//        throw new IOException("Unable to delete file " + tempFile.getPath());
//      }
    }
  }


  public static void getFileVersion(final InputStream sourceStream, final OutputStream outStream, final String revision) throws IOException {
    try (
            final BufferedReader br = new BufferedReader(new InputStreamReader(sourceStream));
            final PipedOutputStream pout = new PipedOutputStream();
            final PipedInputStream pin = new PipedInputStream(pout);
            final OutputStreamWriter outW = new OutputStreamWriter(outStream)) {
      new PrintWriter(outStream).close(); /*clear the file's content*/

      String line;
      boolean isRequestedVersion = false;

      line = br.readLine();
      if (line.startsWith("r")) {
        if (line.substring(1).matches(revision)) {
          isRequestedVersion = true;
        }
        line = br.readLine();
        final int nLines = getReadNumberOfLines(line);
        addLinesToStream(br, pin, pout, 0, nLines);
      }

      while ((((line = br.readLine())) != null) && !isRequestedVersion) {
        if (line.substring(1).matches(revision)) {
          isRequestedVersion = true;
        }
        line = br.readLine();
        final int nLines = getReadNumberOfLines(line);
        for (int i = 0; i < nLines; i++) {
          line = br.readLine();
          String[] numbers = line.split(",");
          final Integer newFileLinesInd = Integer.parseInt(numbers[0].replaceAll("[^0-9]", ""));
          final Integer newFileLinesNum = Integer.parseInt(numbers[1]);
          if (line.startsWith("-")) {
            removeLinesFromStream(pin, pout, newFileLinesInd, newFileLinesNum);
          }
          if (line.startsWith("+")) {
            addLinesToStream(br, pin, pout, newFileLinesInd, newFileLinesNum);
          }
        }
      }

      final byte[] buffer = new byte[1024];
      while (pin.read(buffer) > -1) {
        outW.write(buffer.toString());
      }
    }
  }

  private static int getNumberOfLines(final PipedInputStream pInStream) throws IOException {
    int nLines = 0;
    try (final BufferedReader br = new BufferedReader(new InputStreamReader(pInStream))) {
      while (br.readLine() != null) {
        ++nLines;
      }
    }
    return nLines;
  }

  private static int getLinePos(final PipedInputStream pInStream, final int lineInd) throws IOException {// returns the position of lineInd's line in file
    int linePos = 0;
    if (lineInd == 0) // tmp??
      return 0; // tmp??
    try (final BufferedReader br = new BufferedReader(new InputStreamReader(pInStream))) {
      int r;
      int nLineSeparator = 0;
      while ((r = br.read()) != -1 && nLineSeparator < lineInd - 1) {
        final char ch = (char) r;
        if (ch == '\n') {
          ++nLineSeparator;
        }
        ++linePos;
      }
    }
    return linePos;
  }


  private static ArrayList backtrack(final int[][] lcsLen,
                                     final PipedInputStream pInStream1, final PipedInputStream pInStream2,
                                     final int i, final int j, ArrayList bt) throws IOException {
//    if (i == 0 || j == 0) {
//      return bt;
//    }
//    final RandomAccessFile raf1 = new RandomAccessFile(pInStream1, "rw");
//    final RandomAccessFile raf2 = new RandomAccessFile(pInStream2, "rw");
//    raf1.seek(getLinePos(pInStream1, i));
//    raf2.seek(getLinePos(pInStream2, j));
//    final String f1Line = raf1.readLine();
//    final String f2Line = raf2.readLine();
//    raf1.close();
//    raf2.close();
//    if (f1Line.equals(f2Line)) {
//      bt.add(i);
//      return backtrack(lcsLen, pInStream1, pInStream2, i - 1, j - 1, bt);
//    } else {
//      if (lcsLen[i][j - 1] > lcsLen[i - 1][j]) {
        return backtrack(lcsLen, pInStream1, pInStream2, i, j - 1, bt);
//      } else {
//        return backtrack(lcsLen, pInStream1, pInStream2, i - 1, j, bt);
//      }
//    }
  }

  private static ArrayList LCS(final PipedInputStream pInStream1, final PipedInputStream pInStream2) throws IOException {/*the longest common subsequence*/
    ArrayList<Integer> commonLines = new ArrayList<>();

    try (
            final BufferedReader br1 = new BufferedReader(new InputStreamReader(pInStream1));
            final BufferedReader br2 = new BufferedReader(new InputStreamReader(pInStream2))
    ) {
      final int file1LinesNum = getNumberOfLines(pInStream1);
      final int file2LinesNum = getNumberOfLines(pInStream2);
      final int[][] lcsLen = new int[file1LinesNum][file2LinesNum];
      for (int i = 0; i < file1LinesNum; i++) {
        lcsLen[i][0] = 0;
      }
      for (int j = 0; j < file2LinesNum; j++) {
        lcsLen[0][j] = 0;
      }

      for (int i = 1; i < file1LinesNum; i++) {
        final String f1Line = br1.readLine();
        for (int j = 1; j < file2LinesNum; j++) {
          final String f2Line = br2.readLine();
          if (f1Line.equals(f2Line)) {
            lcsLen[i][j] = lcsLen[i - 1][j - 1] + 1;
          } else {
            lcsLen[i][j] = Math.max(lcsLen[i][j - 1], lcsLen[i - 1][j]);
          }
        }
      }
      commonLines = backtrack(lcsLen, pInStream1, pInStream2, file1LinesNum, file2LinesNum, commonLines);
    }
    return commonLines;
  }

  private static int getReadNumberOfLines(final String line) {
    return Integer.parseInt(line.replaceAll(" operations(.*)", ""));
  }

  private static void addLinesToStream(final BufferedReader br, final PipedInputStream pInStream, final PipedOutputStream pOutStream,
                                       final int linesInd, final int linesNum)
          throws IOException
  {
    try (final ByteArrayOutputStream arrOut = new ByteArrayOutputStream()) {
      final byte[] buffer = new byte[1024];
      int len;
      while ((len = pInStream.read(buffer)) > -1) {
        arrOut.write(buffer, 0, len);
      }
      arrOut.flush();
      pOutStream.flush();

      try (
              final InputStream inStream = new ByteArrayInputStream(arrOut.toByteArray());
              final BufferedReader tmpBr = new BufferedReader(new InputStreamReader(inStream));
              final OutputStreamWriter outW = new OutputStreamWriter(pOutStream)
      ) {
        int nLines = 0;
        String line;
        while (nLines < linesInd) {
          line = tmpBr.readLine();
          outW.write(line + LINE_SEPARATOR);
          ++nLines;
        }

        for (int j = 0; j < linesNum; j++) {
          line = br.readLine();
          outW.write(line + LINE_SEPARATOR);
        }
        while ((line = tmpBr.readLine()) != null) {
          outW.write(line + LINE_SEPARATOR);
        }
      }
    }
  }

  private static void removeLinesFromStream(final PipedInputStream pInStream, final PipedOutputStream pOutStream,
                                            final int linesInd, final int linesNum) throws IOException {
    try (final ByteArrayOutputStream arrOut = new ByteArrayOutputStream()) {
      final byte[] buffer = new byte[1024];
      int len;
      while ((len = pInStream.read(buffer)) > -1) {
        arrOut.write(buffer, 0, len);
      }
      arrOut.flush();
      pOutStream.flush();

      try (
              final InputStream inStream = new ByteArrayInputStream(arrOut.toByteArray());
              final BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
              final OutputStreamWriter outW = new OutputStreamWriter(pOutStream)
      ) {
        int nLines = 0;
        String line;

        while ((line = br.readLine()) != null) {
          if (linesInd + linesNum <= nLines || nLines < linesInd) {
            outW.write(line + LINE_SEPARATOR);
          }
          ++nLines;
        }
      }
    }
  }
}
