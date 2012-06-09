package usage;

import java.io.Serializable;

/**
 * @author pmdusso
 * @version 1.0 @created 24-abr-2012 15:22:16
 */
public class MemData implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8926305540190678396L;

	public MemData(
            int size,
            int resident,
            int share,
            int text,
            int data,
            String vsize,
            int rss,
            String rsslim,
            int memTotal,
            int memUsed,
            int memFree,
            int memBuffers,
            int memCached) {
        this.data = data;
        this.memBuffers = memBuffers;
        this.memCached = memCached;
        this.memFree = memFree;
        this.memTotal = memTotal;
        this.memUsed = memUsed;
        this.resident = resident;
        this.rsslim = rsslim;
        this.rss = rss;
        this.share = share;
        this.size = size;
        this.text = text;
        this.vsize = vsize;
    }

    /**
     * SIZE, RESIDENT, SHARE, TEXT, DATA, VIRTUALSIZE, RSS, RSSLIM, MEM_TOTAL,
     * MEM_USED, MEM_FREE, MEM_BUFFERS, MEM_CACHED).
     */
    @Override
    public String toString() {
        return String.valueOf(this.size + ", ")
                + String.valueOf(this.resident + ", ")
                + String.valueOf(this.share + ", ")
                + String.valueOf(this.text + ", ")
                + String.valueOf(this.data + ", ")
                + String.valueOf(this.vsize + ", ")
                + String.valueOf(this.rss + ", ")
                + String.valueOf(this.rsslim + ", ")
                + String.valueOf(this.memTotal + ", ")
                + String.valueOf(this.memUsed + ", ")
                + String.valueOf(this.memFree + ", ")
                + String.valueOf(this.memBuffers + ", ")
                + String.valueOf(this.memCached);
    }
    /**
     * Total number of pages of memory (total program size in kilobytes).
     */
    int size;
    /**
     * Resident set size.
     */
    private int resident;
    /**
     * Number of pages of shared (mmap'd) memory
     */
    private int share;
    /**
     * Number of pages that are code (text).
     */
    private int text;
    /**
     * Number of library pages (unused in Linux 2.6).
     */
    //private int lib;
    /**
     * Number of pages of data + stack.
     */
    private int data;
    /**
     * Dirty pages (unused in Linux 2.6).
     */
    //private int dt;
    /**
     * Virtual memory size in bytes (/proc/[pid]/stat).
     */
    private String vsize;
    /**
     * Resident set number of pages the process has in real memory. This is just
     * the pages which count toward text, data, or stack space. This does not
     * include pages which have not been demand-loaded in, or which are swapped
     * out (/proc/[pid]/stat).
     */
    private int rss;
    /**
     * Current limit (in bytes) of the rss of the process; usually 2,147,483,647
     * (/proc/[pid]/stat).
     */
    String rsslim;
    /**
     * Total memory phisicaly instaled in the system. This is not a usage
     * information, however, can be useful to calculate metrics over the
     * process, in KB.
     */
    private int memTotal;
    /**
     * Total memory being used by the hole system, not only the WattDB process,
     * in kilobytes.
     */
    private int memUsed;
    /**
     * The amount of physical RAM, in kilobytes, left unused by the system.
     */
    private int memFree;
    /**
     * The amount of physical RAM, in kilobytes, used for file buffers.
     */
    private int memBuffers;
    /**
     * The amount of physical RAM, in kilobytes, used as cache memory.
     */
    private int memCached;

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @return the resident
     */
    public int getResident() {
        return resident;
    }

    /**
     * @return the share
     */
    public int getShare() {
        return share;
    }

    /**
     * @return the text
     */
    public int getText() {
        return text;
    }

    /**
     * @return the data
     */
    public int getData() {
        return data;
    }

    /**
     * @return the vsize
     */
    public String getVsize() {
        return vsize;
    }

    /**
     * @return the rss
     */
    public int getRss() {
        return rss;
    }

    /**
     * @return the rsslim
     */
    public String getRsslim() {
        return rsslim;
    }

    /**
     * @return the memTotal
     */
    public int getMemTotal() {
        return memTotal;
    }

    /**
     * @return the memUsed
     */
    public int getMemUsed() {
        return memUsed;
    }

    /**
     * @return the memFree
     */
    public int getMemFree() {
        return memFree;
    }

    /**
     * @return the memBuffers
     */
    public int getMemBuffers() {
        return memBuffers;
    }

    /**
     * @return the memCached
     */
    public int getMemCached() {
        return memCached;
    }
}// end MemData_t