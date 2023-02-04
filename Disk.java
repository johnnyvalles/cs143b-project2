public class Disk {
    public static final int TOTAL_BLOCKS = 1024;
    public static final int BLOCK_SIZE = 512;
    private int[][] disk;

    public Disk() {
        this.disk = new int[Disk.TOTAL_BLOCKS][Disk.BLOCK_SIZE];
    }

    public void readBlock(int blockNumber, int pmAddress, int[] pm) {
        for (int i = 0; i < Disk.BLOCK_SIZE; ++i) {
            pm[pmAddress + i] = this.disk[blockNumber][i];
        }
    }
}