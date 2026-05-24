package huffman;

import java.io.*;
import java.util.Map;

public class HuffmanEncoder {

    public static final byte[] MAGIC = {'H', 'U', 'F', 'F'};

    public static void encode(File inputFile, File outputFile) throws IOException {
        byte[] input = readAll(inputFile);

        long[] freq = new long[256];
        for (byte b : input) freq[b & 0xFF]++;

        HuffmanNode root = HuffmanTree.build(freq);
        Map<Integer, String> codes = HuffmanTree.buildCodeBook(root);

        StringBuilder bits = new StringBuilder();
        for (byte b : input) {
            bits.append(codes.get(b & 0xFF));
        }

        int padBits = (8 - (bits.length() % 8)) % 8;
        for (int i = 0; i < padBits; i++) bits.append('0');

        byte[] data = bitsToBytes(bits.toString());

        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(outputFile)))) {

            out.write(MAGIC);

            out.writeLong(input.length);

            out.writeShort(codes.size());

            for (Map.Entry<Integer, String> e : codes.entrySet()) {
                int sym  = e.getKey();
                String code = e.getValue();
                int len  = code.length();
                int codeBytes = (len + 7) / 8;

                out.writeByte(sym);
                out.writeByte(len);

                String padded = String.format("%" + (codeBytes * 8) + "s", code).replace(' ', '0');
                out.write(bitsToBytes(padded));
            }

            out.writeByte(padBits);

            out.write(data);
        }

        System.out.printf("Encoded: %s -> %s%n", inputFile.getName(), outputFile.getName());
        System.out.printf("  Original size : %d bytes%n", input.length);
        System.out.printf("  Encoded size  : %d bytes%n", outputFile.length());
        System.out.printf("  Ratio         : %.2f%%%n",
                100.0 * outputFile.length() / Math.max(1, input.length));
        System.out.printf("  Symbols in dict: %d%n", codes.size());
        System.out.println("  Code table:");
        codes.entrySet().stream()
             .sorted(Map.Entry.comparingByKey())
             .forEach(e -> System.out.printf("    0x%02X ('%s')  freq=%d  code=%s%n",
                     e.getKey(),
                     (e.getKey() >= 32 && e.getKey() < 127) ? (char)(int)e.getKey() : ".",
                     freq[e.getKey()],
                     e.getValue()));
    }


    private static byte[] readAll(File f) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(f))) {
            return is.readAllBytes();
        }
    }

    static byte[] bitsToBytes(String bits) {
        int n = bits.length() / 8;
        byte[] result = new byte[n];
        for (int i = 0; i < n; i++) {
            int v = 0;
            for (int j = 0; j < 8; j++) {
                v = (v << 1) | (bits.charAt(i * 8 + j) - '0');
            }
            result[i] = (byte) v;
        }
        return result;
    }
}
