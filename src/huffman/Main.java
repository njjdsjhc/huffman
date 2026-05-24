package huffman;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            printUsage();
            System.exit(1);
        }

        String cmd  = args[0].toLowerCase();
        File input  = new File(args[1]);
        File output = new File(args[2]);

        if (!input.exists()) {
            System.err.println("Error: input file not found: " + input);
            System.exit(2);
        }

        switch (cmd) {
            case "encode" -> HuffmanEncoder.encode(input, output);
            case "decode" -> HuffmanDecoder.decode(input, output);
            default -> {
                System.err.println("Unknown command: " + cmd);
                printUsage();
                System.exit(1);
            }
        }
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java -cp out huffman.Main encode <input>  <output.huff>");
        System.out.println("  java -cp out huffman.Main decode <input.huff> <output>");
    }
}
