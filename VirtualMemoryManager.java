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
        
        int pageTableLocation = this.pm.memory[2 * segmentNumber + 1];
        if (pageTableLocation < 0) {
            // page fault! segment's page table is not resident
            // need to allocate frame and read block
            pageTableLocation = pageFault(-pageTableLocation);
            this.pm.memory[2 * segmentNumber + 1] = pageTableLocation;
            // segment's page table now resident
        }

        int pageLocation = this.pm.memory[pageTableLocation * VirtualMemoryManager.PAGE_SIZE + pageTableEntry];
        if (pageLocation < 0) {
            // page fault! page not resident
            pageLocation = pageFault(-pageLocation);
            this.pm.memory[pageTableLocation * VirtualMemoryManager.PAGE_SIZE + pageTableEntry] = pageLocation;
        }

        return pageLocation * VirtualMemoryManager.PAGE_SIZE + pageOffset;
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
        int pageTableLocation = stEntry[2];

        if (pageTableLocation > 0) {
            this.freeFrames.removeFirstOccurrence(pageTableLocation);
        }

        this.pm.write(2 * segmentNumber, segmentSize);
        this.pm.write(2 * segmentNumber + 1, pageTableLocation);
    }

    private void setPageTableEntry(int[] ptEntry) {
        int segmentNumber = ptEntry[0];
        int pageNumber = ptEntry[1];
        int pageLocation = ptEntry[2];
        int pageTableLocation = this.pm.read(2 * segmentNumber + 1);

        if (pageTableLocation < 0) {
            // segment's page table is not resident
            // segment's page table is found on disk at block |pageTableLocation|
            this.disk.disk[-pageTableLocation][pageNumber] = pageLocation;
            this.pm.write(2 * segmentNumber + 1, pageTableLocation);
        } else {
            // segment's page table is resident in memory
            // segment's page table is found at frame number pageTableLocation
            this.freeFrames.removeFirstOccurrence(pageTableLocation);
            this.pm.memory[pageTableLocation * 512 + pageNumber] = pageLocation;
        }

        if (pageLocation > 0) {
            this.freeFrames.removeFirstOccurrence(pageLocation);
        }
    }

    private int pageFault(int blockNumber) {
        // allocate a physical memory frame
        int frame = this.freeFrames.removeFirst();
        // read block into physical memory at frame & return frame
        this.disk.readBlock(blockNumber, frame * VirtualMemoryManager.PAGE_SIZE, this.pm.memory);
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

    public String debugSegmentTable() {
        String str = "";
        str += "**********************" + System.lineSeparator();
        str += "Segment Table" + System.lineSeparator();
        str += "**********************" + System.lineSeparator();
        for (int i = 0; i < VirtualMemoryManager.PAGE_SIZE; ++i) {
            str += "[" + i + "]: (" + this.pm.memory[2 * i] + ", " + this.pm.memory[2 * i + 1] + ")";
            str += System.lineSeparator();
        }
        return str;
    }
    
    public String debugSegmentTableEntry(int entry) {
        String str = "";
        str += "ST[" + entry + "]: (" + this.pm.memory[2 * entry] + ", " + this.pm.memory[2 * entry + 1] + ")";
        return str;
    }

    @Override
    public String toString() {
        return this.pm.toString();
    }
}