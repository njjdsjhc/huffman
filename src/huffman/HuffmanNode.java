package huffman;

public class HuffmanNode implements Comparable<HuffmanNode> {
    public final int symbol;   
    public final long freq;
    public final HuffmanNode left;
    public final HuffmanNode right;

    public HuffmanNode(int symbol, long freq) {
        this.symbol = symbol;
        this.freq   = freq;
        this.left   = null;
        this.right  = null;
    }

    public HuffmanNode(HuffmanNode left, HuffmanNode right) {
        this.symbol = -1;
        this.freq   = left.freq + right.freq;
        this.left   = left;
        this.right  = right;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    @Override
    public int compareTo(HuffmanNode o) {
        return Long.compare(this.freq, o.freq);
    }
}
