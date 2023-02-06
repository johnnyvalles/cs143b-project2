public class PhysicalMemory {
    public int[] memory;
    
    // words (4-bytes) in memory
    public static final int SIZE = 524288;

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