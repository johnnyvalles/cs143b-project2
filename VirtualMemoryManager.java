import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.LinkedList;

public class VirtualMemoryManager {
    public static final int PAGE_SIZE = 512;
    private PhysicalMemory pm;
    private LinkedList<Integer> freeFrames;
    private Disk disk;
    
    public VirtualMemoryManager() {
        this.pm = new PhysicalMemory();
        this.freeFrames = new LinkedList<Integer>();
        this.disk = new Disk();
    }

    public int translateVAtoPA(int virtualAddress) {
        int segmentNumber = virtualAddress >> 18;
        int segmentSize = this.pm.read(2 * segmentNumber);
        int pageTableEntry = ((virtualAddress >> 9) & 0x01FF);
        int pageOffset = virtualAddress & 0x01FF;
        int pw = virtualAddress & 0x3FFFF;

        if (pw >= segmentSize) {
            // segment access out of bounds
            return -1;
        }
        
        int pageTableFrameNumber = this.pm.read(2 * segmentNumber + 1);
        int pageTableStart = pageTableFrameNumber * VirtualMemoryManager.PAGE_SIZE;
        int pageLocation = this.pm.read(pageTableStart + pageTableEntry);

        if (pageLocation < 0) {
            // page fault! page not resident
            pageLocation = pageFault(-pageLocation);
            this.pm.write(pageTableStart + pageTableEntry, pageLocation);
        }

        int pageStart = pageLocation * 512;

        return pageStart + pageOffset;
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
            freeFrames.add(frameNumber);
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
        int segmentNumber = stEntry[0];
        int segmentSize = stEntry[1];
        int ptLocation = stEntry[2];

        if (ptLocation > 0) {
            this.freeFrames.removeFirstOccurrence(ptLocation); 
        }

        this.pm.write(2 * segmentNumber, segmentSize);
        this.pm.write(2 * segmentNumber + 1, ptLocation);
    }

    private void setPageTableEntry(int[] ptEntry) {
        int segmentNumber = ptEntry[0];
        int pageNumber = ptEntry[1];
        int pageLocation = ptEntry[2];
        int pageTableLocation = this.pm.read(2 * segmentNumber + 1);

        if (pageTableLocation < 0) {
            // page fault! page table not resident
            pageTableLocation = this.pageFault(-pageTableLocation);
            this.pm.write(2 * segmentNumber + 1, pageTableLocation);
        }

        if (pageLocation > 0) {
            this.freeFrames.removeFirstOccurrence(pageLocation);
        }

        this.pm.write(pageTableLocation * VirtualMemoryManager.PAGE_SIZE + pageNumber, pageLocation);
    }

    private int pageFault(int blockNumber) {
        int frame = this.freeFrames.removeFirst();
        int frameStart = frame * 512;
        this.disk.readBlock(blockNumber, frameStart, this.pm.memory);
        return frame;
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