public class PhysicalMemory {
    private int[] memory;
    private int size;
    private static final int DEFAULT_MEMORY_SIZE = 524288;

    public PhysicalMemory(int size) {
        try {
            isValidMemorySize(size);
            System.out.println("HERE");
        } catch(Exception error) {
            System.err.println(error);
            System.exit(1);
        }

        this.memory = new int[size];
        this.size = size;
    }

    public PhysicalMemory() {
        this.memory = new int[PhysicalMemory.DEFAULT_MEMORY_SIZE];
        this.size = PhysicalMemory.DEFAULT_MEMORY_SIZE;
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

    private void isValidAddress(int address) {
        if (address < 0 || address >= this.size) {
            throw new IllegalAccessError("Invalid memory access at: " + address);
        }
    }

    private void isValidMemorySize(int size) {
        if (size <= 0 || (size & (size - 1)) != 0) {
            throw new IllegalArgumentException("Physical memory size must be a power of 2.");
        }
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append("************************" + System.lineSeparator());
        str.append("PHYSICAL MEMORY" + System.lineSeparator());
        str.append("************************" + System.lineSeparator());

        for (int i = 0; i < this.size; ++i) {
            str.append(i + ": " + this.memory[i] + System.lineSeparator());
        }
        str.append("************************" + System.lineSeparator());

        return str.toString();
    }
}