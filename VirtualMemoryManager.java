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

    public void init() {
        // triples of form (s, z, f/b)
        int[] st_init = { 0, 900, 2, 1, 262000, 5, 2, 1100, -100, 3, 1025, 3 };

        // triples of form (s, p, f/b)
        int[] pages_init = { 0, 0, 4, 0, 1, 6, 1, 0, 9, 1, 511, 10, 2, 0, 11, 2, 1, 12, 2, 2, -24, 3, 0, 7, 3, 1, -25, 3, 2, 8 };

        // init segment table entries
        for (int i = 0; i < st_init.length; i += 3) {
            int segment = st_init[i];
            int segmentSize = st_init[i + 1];
            int ptLocation = st_init[i + 2];

            this.pm.write(2 * segment, segmentSize);
            this.pm.write(2 * segment + 1, ptLocation);
        }

        // init pages
        for (int i = 0; i < pages_init.length; i += 3) {
            int segment = pages_init[i];
            int pageNumber = pages_init[i + 1];
            int pageLocation = pages_init[i + 2];

            int segmentPTStart = this.pm.read(2 * segment + 1) * VirtualMemoryManager.PAGE_SIZE;
            this.pm.write(segmentPTStart + pageNumber, pageLocation);
        }
    }
}