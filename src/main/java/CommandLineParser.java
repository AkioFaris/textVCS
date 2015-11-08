import org.apache.commons.cli2.*;
import org.apache.commons.cli2.builder.*;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.util.HelpFormatter;
import java.io.*;

/**
 * Created by Akio on 12/10/2015.
 */
public class CommandLineParser {
  public static final DefaultOptionBuilder oBuilder;
  public static final ArgumentBuilder aBuilder;
  public static final GroupBuilder gBuilder;

  public static final Option file;
  public static final Option inputFile;
  public static final Option outputFile;
  public static final Option sourceFile;
  public static final Option revision;

  public static final Option help;
  public static final Option create;
  public static final Option update;
  public static final Option get;

  public static final Group commandOptions;

  public static Parser parser;
  public static CommandLine cl;

  public static String iFilename = null;
  public static String oFilename = null;
  public static String sFilename = null;
  public static String rIndex = null;

  public static InputStream inStream;
  public static OutputStream outStream;

  static {
    oBuilder = new DefaultOptionBuilder();
    aBuilder = new ArgumentBuilder();
    gBuilder = new GroupBuilder();
    file =
            aBuilder
                    .withName("file")
                    .withMinimum(1)
                    .withMaximum(1)
                    .create();
    inputFile =
            oBuilder
                    .withShortName("i")
                    .withDescription("use given *.txt file as an input")
                    .withArgument((Argument) file)
                    .create();
    outputFile =
            oBuilder
                    .withShortName("o")
                    .withDescription("create an output file with this filename")
                    .withArgument((Argument) file)
                    .create();
    sourceFile =
            oBuilder
                    .withShortName("s")
                    .withDescription("use given *.vc file as a source")
                    .withArgument((Argument) file)
                    .create();
    revision =
            oBuilder
                    .withShortName("r")
                    .withDescription("set revision message")
                    .withArgument(
                            aBuilder
                                    .withName("revision")
                                    .withMinimum(1)
                                    .withMaximum(1)
                                    .create())
                    .create();
    help =
            oBuilder
                    .withLongName("help")
                    .withDescription("print help")
                    .create();
    create =
            oBuilder
                    .withLongName("create")
                    .withDescription("create a *.vc file")
                    .withChildren(
                            gBuilder
                                    .withOption(inputFile)
                                    .withOption(outputFile)
                                    .withOption(revision)
                                    .withMinimum(3)
                                    .withMaximum(3)
                                    .create()
                    )
                    .create();
    update =
            oBuilder
                    .withLongName("update")
                    .withDescription("update the *.vc file")
                    .withChildren(
                            gBuilder
                                    .withOption(inputFile)
                                    .withOption(outputFile)
                                    .withOption(revision)
                                    .withMinimum(3)
                                    .withMaximum(3)
                                    .create()
                    )
                    .create();
    get =
            oBuilder
                    .withLongName("get")
                    .withDescription("get a *.txt file from the *.vc file")
                    .withChildren(
                            gBuilder
                                    .withOption(sourceFile)
                                    .withOption(outputFile)
                                    .withOption(revision)
                                    .withMinimum(3)
                                    .withMaximum(3)
                                    .create()
                    )
                    .create();
    commandOptions =
            gBuilder
                    .withName("commandOptions")
                    .withOption(help)
                    .withOption(create)
                    .withOption(update)
                    .withOption(get)
                    .withMinimum(1)
                    .withMaximum(1)
                    .create();
    parser = new Parser();
  }


  public static void main(String[] args) throws OptionException, IOException {
    parser.setGroup(commandOptions);
    cl = parser.parse(args);

    if (cl.hasOption(inputFile)) {
      iFilename = (String) cl.getValue(inputFile);
      inStream = new FileInputStream(new File(iFilename));
    }
    if (cl.hasOption(outputFile)) {
      oFilename = (String) cl.getValue(outputFile);
      outStream = new FileOutputStream(new File(oFilename));
    }
    if (cl.hasOption(sourceFile)) {
      sFilename = (String) cl.getValue(sourceFile);
      inStream = new FileInputStream(new File(sFilename));
    }
    if (cl.hasOption(revision)) {
      rIndex = (String) cl.getValue(revision);
    }

    if (cl.hasOption(help)) {
      displayHelp(commandOptions);
    } else if (cl.hasOption(create) && iFilename != null && oFilename != null && rIndex != null) {
      VCS.createVCFile(inStream, outStream, rIndex);
      System.out.println("CREATE I: " + iFilename + " O: " + oFilename + " R: " + rIndex); // for debugging
    } else if (cl.hasOption(update) && iFilename != null && oFilename != null && rIndex != null) {
      VCS.update(inStream, outStream, rIndex);
      System.out.println("UPDATE I: " + iFilename + " S: " + sFilename + " R: " + rIndex); // for debugging
    } else if (cl.hasOption(get) && sFilename != null && oFilename != null && rIndex != null) {
      VCS.getFileVersion(inStream, outStream, rIndex);
      System.out.println("GET S: " + sFilename + " O: " + oFilename + " R: " + rIndex); // for debugging
    } else {
      System.out.println("Input was not recognized.");
      displayHelp(commandOptions);
    }
  }

  private static void displayHelp(final Group options) {
    final HelpFormatter hf = new HelpFormatter();
    hf.setShellCommand("textVCS");
    hf.setGroup(options);
    hf.getFullUsageSettings().add(DisplaySetting.DISPLAY_GROUP_NAME);
    hf.getFullUsageSettings().add(DisplaySetting.DISPLAY_GROUP_ARGUMENT);
    hf.getFullUsageSettings().remove(DisplaySetting.DISPLAY_GROUP_EXPANDED);

    hf.getDisplaySettings().remove(DisplaySetting.DISPLAY_GROUP_ARGUMENT);

    hf.getLineUsageSettings().add(DisplaySetting.DISPLAY_PROPERTY_OPTION);
    hf.getLineUsageSettings().add(DisplaySetting.DISPLAY_PARENT_ARGUMENT);
    hf.getLineUsageSettings().add(DisplaySetting.DISPLAY_ARGUMENT_BRACKETED);

    hf.print();
  }
}
