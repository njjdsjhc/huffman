package huffman;

import java.util.*;

public class HuffmanTree {

    public static HuffmanNode build(long[] freq) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (int i = 0; i < 256; i++) {
            if (freq[i] > 0) {
                pq.add(new HuffmanNode(i, freq[i]));
            }
        }
        if (pq.isEmpty()) return null;

        if (pq.size() == 1) {
            HuffmanNode only = pq.poll();
            pq.add(only);
            pq.add(new HuffmanNode(only.symbol, 0));
        }

        while (pq.size() > 1) {
            HuffmanNode a = pq.poll();
            HuffmanNode b = pq.poll();
            pq.add(new HuffmanNode(a, b));
        }
        return pq.poll();
    }

    public static Map<Integer, String> buildCodeBook(HuffmanNode root) {
        Map<Integer, String> codes = new HashMap<>();
        if (root == null) return codes;
        traverse(root, "", codes);
        return codes;
    }

    private static void traverse(HuffmanNode node, String prefix, Map<Integer, String> codes) {
        if (node.isLeaf()) {
            codes.put(node.symbol, prefix.isEmpty() ? "0" : prefix);
            return;
        }
        traverse(node.left,  prefix + "0", codes);
        traverse(node.right, prefix + "1", codes);
    }
}
