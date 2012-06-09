package usage;

import java.io.Serializable;

/**
 * @author pmdusso
 * @version 1.0 @created 24-abr-2012 15:21:46
 */
public class DiskData implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5394909818404147396L;

	/**
     * @param major
     * @param minor
     * @param name
     * @param readsCompleted
     * @param readsMerged
     * @param writesMerged
     * @param sectorsRead
     * @param millisecondsReading
     * @param writesCompleted
     * @param sectorsWritten
     * @param millisecondsWriting
     * @param iosInProgress
     * @param millisecondsSpentInIO
     * @param weightedMillisecondsDoingIO
     */
    public DiskData(String major, String minor, String name,
            long readsCompleted, long readsMerged, long writesMerged,
            long sectorsRead, long millisecondsReading, long writesCompleted,
            long sectorsWritten, long millisecondsWriting, long iosInProgress,
            long millisecondsSpentInIO, long weightedMillisecondsDoingIO) {
        this.major = major;
        this.minor = minor;
        this.name = name;
        this.readsCompleted = readsCompleted;
        this.readsMerged = readsMerged;
        this.writesMerged = writesMerged;
        this.sectorsRead = sectorsRead;
        this.millisecondsReading = millisecondsReading;
        this.writesCompleted = writesCompleted;
        this.sectorsWritten = sectorsWritten;
        this.millisecondsWriting = millisecondsWriting;
        this.iosInProgress = iosInProgress;
        this.millisecondsSpentInIO = millisecondsSpentInIO;
        this.weightedMillisecondsDoingIO = weightedMillisecondsDoingIO;
    }

    /**
     * PARTITION_NAME, READS_COMPLETED, READS_MERGED, WRITES_MERGED, SECTORS_READ, MILLISECONDS_READING, WRITES_COMPLETED, SECTORS_WRITTEN,
     * MILLISECONDS_WRITING, IO_IN_PROGRESS, MILLISECONDS_SPENT_IN_IO, WEIGHTED_MILLISECONDS_DOING_IO.
     */
    @Override
    public String toString() {
        return String.valueOf("'" + this.name + "'" + ", ")
                + String.valueOf(this.readsCompleted + ", ")
                + String.valueOf(this.readsMerged + ", ")
                + String.valueOf(this.writesMerged + ", ")
                + String.valueOf(this.sectorsRead + ", ")
                + String.valueOf(this.millisecondsReading + ", ")
                + String.valueOf(this.writesCompleted + ", ")
                + String.valueOf(this.sectorsWritten + ", ")
                + String.valueOf(this.millisecondsWriting + ", ")
                + String.valueOf(this.iosInProgress + ", ")
                + String.valueOf(this.millisecondsSpentInIO + ", ")
                + String.valueOf(this.weightedMillisecondsDoingIO);
    }
    /**
     * The major number of the devide with this partition.
     */
    String major;
    /**
     * The minor number of the device with this partition. This serves to
     * separate the partitions into different physical devices and relates to
     * the number at the end of the name of the partition.
     */
    String minor;
    /**
     * The name of the partition.
     */
    String name;
    /**
     * This is the total number of reads completed successfully.
     */
    long readsCompleted;
    /**
     * Reads and writes which are adjacent to each other may be merged for
     * efficiency. thus two 4K reads may become one 8K read before it is
     * ultimately handed to the disk, and so it will be counted (and queued).
     */
    long readsMerged;
    /**
     * See numberReadsMerged documentation.
     */
    long writesMerged;
    /**
     * This is the total number of sector read successfully.
     */
    long sectorsRead;
    /**
     * This is the total number of millisecoPartitionnds spent by all reads (as
     * measured from __make_request() to end_that_request_last() ).
     */
    long millisecondsReading;
    /**
     * This is the total number of writes completed successfully.
     */
    long writesCompleted;
    /**
     * This is the total number of sectors written successfully.
     */
    long sectorsWritten;
    /**
     * This is the total number of milliseconds spent by all writes (as measured
     * from __make_request() to end_that_request_last() ).
     */
    long millisecondsWriting;
    /**
     * The only field that should go to zero. Incremented as requests are given
     * to appropriate struct request_queue and decremented as they finish.
     */
    long iosInProgress;
    /**
     * This field increases so long as field IOs currently in progress is
     * nonzero.
     */
    long millisecondsSpentInIO;
    /**
     * This field is incremented at each I/O start, I/O completion, I/O merge,
     * or read of these stats by the number of I/Os Partitionin progress times
     * the number of milliseconds spent doing I/O since the last update of this
     * field. This can provide an easy measure of both I/O completion time and
     * the backlog that may be accumulating.
     */
    long weightedMillisecondsDoingIO;

    public void finalize() throws Throwable {
    }

    /**
     * @return the major
     */
    public String getMajor() {
        return major;
    }

    /**
     * @return the minor
     */
    public String getMinor() {
        return minor;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the readsCompleted
     */
    public long getReadsCompleted() {
        return readsCompleted;
    }

    /**
     * @return the readsMerged
     */
    public long getReadsMerged() {
        return readsMerged;
    }

    /**
     * @return the writesMerged
     */
    public long getWritesMerged() {
        return writesMerged;
    }

    /**
     * @return the sectorsRead
     */
    public long getSectorsRead() {
        return sectorsRead;
    }

    /**
     * @return the millisecondsReading
     */
    public long getMillisecondsReading() {
        return millisecondsReading;
    }

    /**
     * @return the writesCompleted
     */
    public long getWritesCompleted() {
        return writesCompleted;
    }

    /**
     * @return the sectorsWritten
     */
    public long getSectorsWritten() {
        return sectorsWritten;
    }

    /**
     * @return the millisecondsWriting
     */
    public long getMillisecondsWriting() {
        return millisecondsWriting;
    }

    /**
     * @return the iosInProgress
     */
    public long getIosInProgress() {
        return iosInProgress;
    }

    /**
     * @return the millisecondsSpentInIO
     */
    public long getMillisecondsSpentInIO() {
        return millisecondsSpentInIO;
    }

    /**
     * @return the weightedMillisecondsDoingIO
     */
    public long getWeightedMillisecondsDoingIO() {
        return weightedMillisecondsDoingIO;
    }
}// end DiskData_t