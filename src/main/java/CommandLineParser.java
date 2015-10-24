import org.apache.commons.cli2.*;
import org.apache.commons.cli2.builder.*;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.util.HelpFormatter;

/**
 * Created by Akio on 12/10/2015.
 */
public class CommandLineParser {
  public static void main(String[] args) throws OptionException {
    final DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();
    final ArgumentBuilder aBuilder = new ArgumentBuilder();
    final GroupBuilder gBuilder = new GroupBuilder();

    final Option file =
            aBuilder
                    .withName("file")
                    .withMinimum(1)
                    .withMaximum(1)
                    .create();
    final Option inputFile =
            oBuilder
                    .withShortName("i")
                    .withDescription("use given *.txt file as an input")
                    .withArgument((Argument) file)
                    .create();
    final Option outputFile =
            oBuilder
                    .withShortName("o")
                    .withDescription("create an output file with this filename")
                    .withArgument((Argument) file)
                    .create();
    final Option sourceFile =
            oBuilder
                    .withShortName("s")
                    .withDescription("use given *.vc file as a source")
                    .withArgument((Argument) file)
                    .create();
    final Option revision =
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

    final Option help =
            oBuilder
                    .withLongName("help")
                    .withDescription("print help")
                    .create();
    final Option create =
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
    final Option update =
            oBuilder
                    .withLongName("update")
                    .withDescription("update the *.vc file")
                    .withChildren(
                            gBuilder
                                    .withOption(inputFile)
                                    .withOption(sourceFile)
                                    .withOption(revision)
                                    .withMinimum(3)
                                    .withMaximum(3)
                                    .create()
                    )
                    .create();
    final Option get =
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

    final Group commandOptions =
            gBuilder
                    .withName("commandOptions")
                    .withOption(help)
                    .withOption(create)
                    .withOption(update)
                    .withOption(get)
                    .withMinimum(1)
                    .withMaximum(1)
                    .create();

    Parser parser = new Parser();
    parser.setGroup(commandOptions);
    CommandLine cl = parser.parse(args);

    String iFilename = null;
    String oFilename = null;
    String sFilename = null;
    String rIndex = null;

    if (cl.hasOption(help)) {
      displayHelp(commandOptions);
      return;
    }

    if (cl.hasOption(inputFile)) {
      iFilename = (String) cl.getValue(inputFile);
    }
    if (cl.hasOption(outputFile)) {
      oFilename = (String) cl.getValue(outputFile);
    }
    if (cl.hasOption(sourceFile)) {
      sFilename = (String) cl.getValue(sourceFile);
    }
    if (cl.hasOption(revision)) {
      rIndex = (String) cl.getValue(revision);
    }

    if (cl.hasOption(create) && iFilename != null && oFilename != null && rIndex != null) {
      VCS.createVCFile(iFilename, oFilename, rIndex);
      System.out.println("CREATE I: " + iFilename + " O: " + oFilename + " R: " + rIndex); // for debugging
    }
    if (cl.hasOption(update) && iFilename != null && sFilename != null && rIndex != null) {
      VCS.update(iFilename, sFilename, rIndex);
      System.out.println("UPDATE I: " + iFilename + " S: " + sFilename + " R: " + rIndex); // for debugging
    }
    if (cl.hasOption(get) && sFilename != null && oFilename != null && rIndex != null) {
      VCS.getFileVersion(sFilename, oFilename, rIndex);
      System.out.println("GET S: " + sFilename + " O: " + oFilename + " R: " + rIndex); // for debugging
    }
  }

  private static void displayHelp(final Group options) {
    HelpFormatter hf = new HelpFormatter();
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
