/**
 * Aveiro University, Department of Electronics, Telecommunications and
 * Informatics. MIECT - Information Retrieval 
 * Miguel Vicente, 63832 Vasco Santos, 64191
 */
package ri_p2_63832_64191;

import java.io.File;
import java.util.Locale;
import org.apache.commons.cli.*;

/**
 * Main Program for Information Retrieval Assignment 2.
 * @author vsantos, mvicente
 */
public class Ri_p2_63832_64191 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Locale.setDefault(new Locale("en", "US"));

        Options options = new Options();

        // Possible arguments definition
        options.addOption("i", "indexer", true, "Index collection");
        //options.addOption("iF", "inFiles", true, "Input Indexer path");
        //options.addOption("oF", "outFiles", true, "Output files name");
        options.addOption("dsw", "dstopWords", false, "Default Stop Words");
        options.addOption("nsw", "nstopWords", true, "New Stop Words");
        options.addOption("ps", "porterStemmer", false, "Porter Stemmer");
        options.addOption("q", "query", false, "Query Searching Type");
        options.addOption("m", "memory", true, "Max available memory");

        // Execution usage
        HelpFormatter formatter = new HelpFormatter();
        String usage = "./run.sh [-i <path>]"; // [-iF <indexing_results_path]\n";
        //usage += "\t[-oF <output_results_name] [-m <available_memory>\n";
        usage += "[-m <available_memory>]\n";
        usage += "\t[-dsw] [-nsw <stopWords_path>] [-ps] [-q]";
        String header = "\nPossible commands to execute are:";
        String footer = "\nPossible executions: \n";
        footer += "Considering ./run = java -Xmx512M -jar dist/ri_p2_63832_64191.jar\n\n";
        footer += "Simple indexing -> ./run.sh -i <path> -m 512";
        footer += "\nIndexing with default SW and Porter Stemmer";
        footer += " -> ./run.sh -i <path> -m 512 -dsw -ps";
        footer += "\nQuery: ./run.sh -q -dsw -ps";

        // Argument Parsing
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (args.length < 1) {
                formatter.printHelp(usage, header, options, footer);
            } else {
                int memoryToUse;
                if (cmd.hasOption("m")) {
                    memoryToUse = Integer.parseInt(cmd.getOptionValue("m"));
                } else {
                    memoryToUse = 512;
                }
                // Index a collection
                if (cmd.hasOption("i")) {
                    String collectionPath = cmd.getOptionValue("i");
                    File f = new File(collectionPath);
                    if (!f.isDirectory()) {
                        System.out.println("Invalid Document collection path!");
                        System.exit(0);
                    } else {
                        Processor p = null;
                        // Index using default SW
                        if (cmd.hasOption("dsw")) {
                            // Use Stemmer
                            if (cmd.hasOption("ps")) {
                                p = new Processor(cmd.getOptionValue("i"), memoryToUse, 3);
                            } else {
                                p = new Processor(cmd.getOptionValue("i"), memoryToUse, 1);
                            }
                        } // Index using new Stop Words
                        else if (cmd.hasOption("nsw")) {
                            f = new File(cmd.getOptionValue("nsw"));
                            if (!f.isFile()) {
                                System.out.println("Invalid Stop words file path!");
                                System.exit(0);
                            } else {
                                // Use Stemmer
                                if (cmd.hasOption("ps")) {
                                    p = new Processor(cmd.getOptionValue("i"), memoryToUse, 3, cmd.getOptionValue("nsw"));
                                } else {
                                    p = new Processor(cmd.getOptionValue("i"), memoryToUse, 1, cmd.getOptionValue("nsw"));
                                }
                            }
                        } // Index using Porter Stemmer
                        else if (cmd.hasOption("ps")) {
                            p = new Processor(cmd.getOptionValue("i"), memoryToUse, 2);
                        } // Simple Indexing
                        else {
                            p = new Processor(cmd.getOptionValue("i"), memoryToUse, 0);
                        }
                        p.start();
                    }
                } else if (cmd.hasOption("q")) {
                    System.out.println("QUERY MODE\n");
                    SearchProcessor sp = null;
                    // Default Stop Words
                    if (cmd.hasOption("dsw")) {
                        // Use Stemmer
                        if (cmd.hasOption("ps")) {
                            sp = new SearchProcessor(footer, "doc", memoryToUse, 3);
                        } else {
                            sp = new SearchProcessor(footer, "doc", memoryToUse, 1);
                        }
                    } else if (cmd.hasOption("nsw")) {
                        File f = new File(cmd.getOptionValue("nsw"));
                        if (!f.isFile()) {
                            System.out.println("Invalid Stop words file path!");
                            System.exit(0);
                        } else {
                            // Use Stemmer
                            if (cmd.hasOption("ps")) {
                                sp = new SearchProcessor(footer, "doc", memoryToUse, 3, cmd.getOptionValue("nsw"));
                            } else {
                                sp = new SearchProcessor(footer, "doc", memoryToUse, 1, cmd.getOptionValue("nsw"));
                            }
                        }
                    } // Query using Porter Stemmer
                    else if (cmd.hasOption("ps")) {
                        sp = new SearchProcessor(footer, "doc", memoryToUse, 2);
                    } // Simple Query
                    else{
                        sp = new SearchProcessor(footer, "doc", memoryToUse, 0);
                    }
                    sp.start();

                } else {
                    // No valid Processor
                    formatter.printHelp(usage, header, options, footer);
                }
            }
        // Argument parsing excetion.
        } catch (ParseException exp) {
            formatter.printHelp(usage, header, options, footer);
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }
    }    
}