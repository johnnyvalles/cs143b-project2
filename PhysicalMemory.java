public class PhysicalMemory {
    private int[] memory;
    private static final int WORD_SIZE = 4;
    private static final int TOTAL_WORDS = 524288;
    private static final int SIZE = PhysicalMemory.TOTAL_WORDS * PhysicalMemory.WORD_SIZE;

    public PhysicalMemory() {
        this.memory = new int[PhysicalMemory.SIZE];
    }

    public void write(int address, int value) {
        try {
            isValidAddress(address);
        } catch (IllegalAccessError error) {
            System.err.println(error);
            System.exit(-1);
        }

        this.memory[address] = value;
    }

    public int read(int address) {
        try {
            isValidAddress(address);
        } catch (IllegalAccessError error) {
            System.err.println(error);
            System.exit(-1);
        }

        return this.memory[address];
    }

    public void isValidAddress(int address) {
        if (address < 0 || address >= PhysicalMemory.SIZE) {
            throw new IllegalAccessError("Invalid memory access at: " + address);
        }
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < PhysicalMemory.SIZE; ++i) {
            str.append(i + ": " + this.memory[i] + System.lineSeparator());
        }
        return str.toString();
    }
}