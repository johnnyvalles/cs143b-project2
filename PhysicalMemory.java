public class PhysicalMemory {
    private int[] memory;
    private static final int SIZE = 524288;

    public PhysicalMemory() {
        this.memory = new int[PhysicalMemory.SIZE];
    }

    public void write(int address, int value) {
        try {
            isValidAddress(address);
        } catch (IllegalAccessError error) {
            System.err.println(error);
            System.exit(2);
        }

        this.memory[address] = value;
    }

    public int read(int address) {
        try {
            isValidAddress(address);
        } catch (IllegalAccessError error) {
            System.err.println(error);
            System.exit(3);
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

        str.append("************************" + System.lineSeparator());
        str.append("PHYSICAL MEMORY" + System.lineSeparator());
        str.append("************************" + System.lineSeparator());

        for (int i = 0; i < PhysicalMemory.SIZE; ++i) {
            str.append(i + ": " + this.memory[i] + System.lineSeparator());
        }
        str.append("************************" + System.lineSeparator());

        return str.toString();
    }
}