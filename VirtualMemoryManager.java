import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.LinkedList;

public class VirtualMemoryManager {
    public static final int PAGE_SIZE = 512;
    private PhysicalMemory pm;
    private LinkedList<Integer> freeFrames;
    
    public VirtualMemoryManager() {
        this.pm = new PhysicalMemory();
        this.freeFrames = new LinkedList<Integer>();
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
            // segment access out of bounds
            return -1;
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
        
        // init list of free frames
        // frames 0 and 1 are reserved for ST
        final int FRAME_COUNT = PhysicalMemory.SIZE / VirtualMemoryManager.PAGE_SIZE;
        for (int frameNumber = 2; frameNumber < FRAME_COUNT; ++frameNumber) {
            freeFrames.append(frameNumber);
        }

        try {
            scanner = new Scanner(initFile);

            // process segment table tiples
            // triple[0] -> segment number
            // triple[1] -> segment size
            // triple[2] -> segment's PT frame number
            lineScanner = new Scanner(scanner.nextLine());
            for (int i = 0; lineScanner.hasNextInt(); ++i) {
                triple[i % 3] = lineScanner.nextInt();

                if (i % 3 == 2) {
                    setSegmentTableEntry(triple);
                }
            }
            lineScanner.close();
            lineScanner = null;

            // process page table triples
            // triple[0] -> segment number
            // triple[1] -> page number
            // triple[2] -> page's frame number
            lineScanner = new Scanner(scanner.nextLine());
            for (int i = 0; lineScanner.hasNextInt(); ++i) {
                triple[i % 3] = lineScanner.nextInt();

                if (i % 3 == 2) {
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

    private void setSegmentTableEntry(int[] stEntry) {
        int segment = stEntry[0];
        int segmentSize = stEntry[1];
        int ptLocation = stEntry[2];

        this.pm.write(2 * segment, segmentSize);
        this.pm.write(2 * segment + 1, ptLocation);
    }

    private void setPageTableEntry(int[] ptEntry) {
        int segment = ptEntry[0];
        int pageNumber = ptEntry[1];
        int pageLocation = ptEntry[2];
        int segmentPTStart = this.pm.read(2 * segment + 1) * VirtualMemoryManager.PAGE_SIZE;
        this.pm.write(segmentPTStart + pageNumber, pageLocation);
    }

    public void translateFromFile(String filePath) {
        File initFile = new File(filePath);
        Scanner scanner;
        int virtualAddress;

        try {
            scanner = new Scanner(initFile);

            while (scanner.hasNextInt()) {
                virtualAddress = scanner.nextInt();
                System.out.print(this.translateVAtoPA(virtualAddress) + " ");
            }

            scanner.close();
            scanner = null;
        } catch (FileNotFoundException error) {
            System.err.println(error);
            System.exit(-1);
        }
    }

    @Override
    public String toString() {
        return this.pm.toString();
    }
}