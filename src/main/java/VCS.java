import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Objects;

/**
 * Created by Akio on 10/10/2015.
 */
public class VCS {

  public static void createVCFile(final String inputFilename, final String outputFilename,
                                  final String revision) {
    final File newFile;
    try {
      newFile = new File(outputFilename);

      if (!newFile.exists()) {
        if (!newFile.createNewFile()) {
          throw new IOException("Unable to create file: " + outputFilename);
        }
      }

      final FileWriter fw = new FileWriter(outputFilename);
      final FileInputStream fIn = new FileInputStream(inputFilename);
      final BufferedReader br = new BufferedReader(new InputStreamReader(fIn));
      int nLines = 0;

      while (br.readLine() != null) {
        ++nLines;
      }
      final String versionHeader = "r" + revision + System.getProperty("line.separator")
              + Objects.toString(nLines) + " operations";
      fw.write(versionHeader);

      fIn.getChannel().position(0);
      String line;
      while ((line = br.readLine()) != null) {
        fw.append(System.getProperty("line.separator"));
        fw.append(line);
      }

      br.close();
      fw.close();
      fIn.close();
    } catch (Exception e) {
      System.err.println("Exception occurred: " + e.toString());
      e.printStackTrace();
    }
  }

  public static void update(final String inputFilename, final String sourceFilename, final String revision) {
    final int prevRevision;
    final int currevision;
    final Integer[] lineLCSindex = null;
    final int LCSlen;
  }


  public static void getFileVersion(final String sourceFilename, final String outputFilename, final String revision) {
    final File newFile;
    try {
      newFile = new File(outputFilename);

      if (!newFile.exists()) {
        if (!newFile.createNewFile()) {
          throw new IOException("Unable to create file: " + outputFilename);
        }
      } else {
        new PrintWriter(outputFilename).close(); /*clear the file's content*/
      }

      final BufferedReader br = new BufferedReader(new FileReader(sourceFilename));
      String line;
      boolean isRequestedVersion = false;

      line = br.readLine();
      if (line.startsWith("r")) {
        if (line.substring(1).matches(revision)) {
          isRequestedVersion = true;
        }
        line = br.readLine();
        final int nLines = getReadNumberOfLines(line);
        addLinesToFile(br, newFile, 0, nLines);
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
            removeLinesFromFile(newFile, newFileLinesInd, newFileLinesNum);
          }
          if (line.startsWith("+")) {
            addLinesToFile(br, newFile, newFileLinesInd, newFileLinesNum);
          }
        }
      }
      br.close();
    } catch (Exception e) {
      System.err.println("Exception occurred: " + e.toString());
      e.printStackTrace();
    }
  }

  private static void LCS() /*the longest common subsequence*/ {

  }

  private static int getReadNumberOfLines(final String line) {
    return Integer.parseInt(line.replaceAll(" operations(.*)", ""));
  }

  private static void addLinesToFile(final BufferedReader br, final File file, final int linesInd, final int linesNum)
          throws Exception {
    if (!file.exists()) {
      return;
    }
    final File tempFile = new File(file.getAbsolutePath() + ".tmp");
    if (!tempFile.exists()) {
      if (!tempFile.createNewFile()) {
        throw new IOException("Unable to create tmp file: " + tempFile.getPath());
      }
    }
    FileUtils.copyFile(file, tempFile);

    final BufferedReader tmpBr = new BufferedReader(new FileReader(tempFile));
    final FileWriter fw = new FileWriter(file.getPath(), false);

    int nLines = 0;
    String line;
    while (nLines < linesInd) {
      line = tmpBr.readLine();
      fw.append(line).append(System.getProperty("line.separator"));
      ++nLines;
    }

    for (int j = 0; j < linesNum; j++) {
      line = br.readLine();
      fw.append(line + System.getProperty("line.separator"));
    }
    while ((line = tmpBr.readLine()) != null) {
      fw.append(line).append(System.getProperty("line.separator"));
    }

    fw.close();
    tmpBr.close();
    if (!tempFile.delete()) {
      throw new Exception("Unable to delete file " + tempFile.getPath());
    }
  }

  private static void removeLinesFromFile(final File file, final int linesInd, final int linesNum)
          throws Exception {
    if (!file.exists()) {
      return;
    }
    final File tempFile = new File(file.getAbsolutePath() + ".tmp");
    if (!tempFile.exists()) {
      if (!tempFile.createNewFile()) {
        throw new IOException("Unable to create tmp file: " + tempFile.getPath());
      }
    }
    FileUtils.copyFile(file, tempFile);

    final BufferedReader br = new BufferedReader(new FileReader(tempFile));
    final FileWriter fw = new FileWriter(file.getPath(), false);

    int nLines = 0;
    String line;

    while ((line = br.readLine()) != null) {
      if (linesInd + linesNum <= nLines || nLines < linesInd) {
        fw.write(line);
        fw.append(System.getProperty("line.separator"));
      }
      ++nLines;
    }

    fw.close();
    br.close();
    if (!tempFile.delete()) {
      throw new Exception("Unable to delete file " + tempFile.getPath());
    }
  }
}
