package huffman;

import java.io.*;
import java.util.*;

public class HuffmanDecoder {

    public static void decode(File inputFile, File outputFile) throws IOException {

        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(new FileInputStream(inputFile)))) {

            // 1. MAGIC
            byte[] magic = in.readNBytes(4);
            if (!Arrays.equals(magic, HuffmanEncoder.MAGIC)) {
                throw new IOException("Not a HUFF file (bad magic bytes)");
            }

            long origSize = in.readLong();

            int dictSize = in.readUnsignedShort();

            Map<String, Integer> reverseCode = new HashMap<>();
            for (int i = 0; i < dictSize; i++) {
                int sym     = in.readUnsignedByte();
                int codeLen = in.readUnsignedByte();
                int codeBytes = (codeLen + 7) / 8;
                byte[] codeRaw = in.readNBytes(codeBytes);

                String fullBits = bytesToBits(codeRaw);
                String codeBits = fullBits.substring(fullBits.length() - codeLen);
                reverseCode.put(codeBits, sym);
            }

            int padBits = in.readUnsignedByte();

            byte[] data = in.readAllBytes();

            String bits = bytesToBits(data);
            if (padBits > 0 && bits.length() >= padBits) {
                bits = bits.substring(0, bits.length() - padBits);
            }

            byte[] output = decodeBits(bits, reverseCode, origSize);

            try (OutputStream os = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                os.write(output);
            }
        }

        System.out.printf("Decoded: %s -> %s  (%d bytes)%n",
                inputFile.getName(), outputFile.getName(), outputFile.length());
    }


    private static byte[] decodeBits(String bits, Map<String, Integer> reverseCode, long origSize)
            throws IOException {

        if (reverseCode.size() == 1) {
            int sym = reverseCode.values().iterator().next();
            byte[] out = new byte[(int) origSize];
            Arrays.fill(out, (byte) sym);
            return out;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream((int) origSize);
        String buf = "";
        for (int i = 0; i < bits.length(); i++) {
            buf += bits.charAt(i);
            Integer sym = reverseCode.get(buf);
            if (sym != null) {
                baos.write(sym);
                buf = "";
                if (baos.size() == origSize) break;
            }
        }
        if (baos.size() != origSize) {
            throw new IOException(String.format(
                "Decode error: expected %d bytes, got %d", origSize, baos.size()));
        }
        return baos.toByteArray();
    }

    static String bytesToBits(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 8);
        for (byte b : data) {
            int v = b & 0xFF;
            for (int i = 7; i >= 0; i--) {
                sb.append((v >> i) & 1);
            }
        }
        return sb.toString();
    }
}
