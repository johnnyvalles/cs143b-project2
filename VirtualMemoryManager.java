import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class VirtualMemoryManager {
    private PhysicalMemory pm;
    public static final int PAGE_SIZE = 512;

    public VirtualMemoryManager() {
        this.pm = new PhysicalMemory();
    }

    public int translateVAtoPA(int virtualAddress) {
        try {
            this.pm.isValidAddress(virtualAddress);
        } catch (IllegalAccessError error) {
            System.err.println(error);
            System.exit(4);
        }

        int s = virtualAddress >> 18;
        int segmentSize = this.pm.read(2 * s);
        int p = ((virtualAddress >> 9) & 0x01FF);
        int w = virtualAddress & 0x01FF;
        int pw = virtualAddress & 0x3FFFF;

        if (pw >= segmentSize) {
            System.err.println("segment access out of bounds.");
            System.exit(5);
        }
        
        int ptFrameNumber = this.pm.read(2 * s + 1);
        int ptStart = ptFrameNumber * VirtualMemoryManager.PAGE_SIZE;
        int pageFrameNumber = this.pm.read(ptStart + p);
        int pageStart = pageFrameNumber * 512;

        return pageStart + w;
    }

    public void init(String initFilePath) {
        File initFile = new File(initFilePath);
        Scanner scanner;
        Scanner lineScanner;
        int[] triple = new int[3];

        try {
            scanner = new Scanner(initFile);
            // read first line
            lineScanner = new Scanner(scanner.nextLine());
            for (int i = 0; lineScanner.hasNextInt(); ++i) {
                triple[i % 3] = lineScanner.nextInt();

                if (i % 3 == 2) {
                    // System.err.println("(" + triple[0] + ", " + triple[1] + ", " + triple[2] + ")");
                    setSegmentTableEntry(triple);
                }
            }

            lineScanner.close();
            lineScanner = null;

            // read second line
            lineScanner = new Scanner(scanner.nextLine());
            for (int i = 0; lineScanner.hasNextInt(); ++i) {
                triple[i % 3] = lineScanner.nextInt();

                if (i % 3 == 2) {
                    // System.err.println("(" + triple[0] + ", " + triple[1] + ", " + triple[2] + ")");
                    setPageTableEntry(triple);
                }
            }

            lineScanner.close();
            lineScanner = null;
        } catch (FileNotFoundException error) {
            System.err.println(error);
            System.exit(-1);
        }
    }

    void setSegmentTableEntry(int[] stEntry) {
        int segment = stEntry[0];
        int segmentSize = stEntry[1];
        int ptLocation = stEntry[2];

        this.pm.write(2 * segment, segmentSize);
        this.pm.write(2 * segment + 1, ptLocation);
    }

    void setPageTableEntry(int[] ptEntry) {
        int segment = ptEntry[0];
        int pageNumber = ptEntry[1];
        int pageLocation = ptEntry[2];
        int segmentPTStart = this.pm.read(2 * segment + 1) * VirtualMemoryManager.PAGE_SIZE;
        this.pm.write(segmentPTStart + pageNumber, pageLocation);
    }

    @Override
    public String toString() {
        return this.pm.toString();
    }
}