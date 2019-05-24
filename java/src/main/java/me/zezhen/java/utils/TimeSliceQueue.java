package me.zezhen.java.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a fixed time duration sliced into several time slots with equivalent widths. The time duration, a.k.a.,
 * window, can move forward by slots when new record comes, at the same time, old records out of duration window will be
 * dropped.
 * 
 * @author zezhen
 * 
 * @param <T>
 *            Type of records in the queue
 */
public class TimeSliceQueue<T> {

    private static Logger LOG = LoggerFactory.getLogger(TimeSliceQueue.class);

    private int size;
    private int resolution;
    private long startTimestamp;
    private int startIndex = 0;
    private List<T> list;

    /**
     * Instantializes a new <code>TimeSliceQueue</code> using the given <code>size</code> and <code>resolution</code>.
     * 
     * @param size
     *            The size of queue, i.e., the number of time slices
     * @param resolution
     *            The resolution, i.e., the duration of each time slice.
     */
    public TimeSliceQueue(int size, int resolution) {
        this(size, resolution, 0);
    }

    public TimeSliceQueue(int size, int resolutionInSecond, int currentTimestampIndex) {
        this(size, resolutionInSecond, currentTimestampIndex, System.currentTimeMillis());
    }

    public TimeSliceQueue(int size, int resolutionInSecond, int currentTimestampIndex, long currentTimestampInMills) {

        currentTimestampIndex = Math.max(0, Math.min(size - 1, currentTimestampIndex));

        this.startIndex = 0;
        this.size = size;
        this.resolution = resolutionInSecond;
        this.startTimestamp = currentTimestampInMills / 1000 / resolution * resolution - currentTimestampIndex
                * resolution;
        this.list = new ArrayList<T>();

        clean();
    }

    /**
     * Returns the slot index of the specified timestamp.
     * 
     * @param timestamp
     *            The timestamp for seeking the corresponding slot index.
     * @return The index of given timestamp, which will be -1 if timestamp exceeds the current duration of queue
     */
    public int fit(long timestamp) {
        return fit(timestamp, false);
    }

    /**
     * Returns the slot index of the specified timestamp. In append mode, the queue will slide its valid time duration
     * window and discard expired records to cover the query timestamp if it exceeds the current window.
     * 
     * @param timestamp
     *            The timestamp for seeking the corresponding slot index
     * @param append
     *            True if the slot corresponding to the specified timestamp is forced to be appended to the queue
     * @return The index of given timestamp, which will be -1 if timestamp exceeds the current duration of queue, but
     *         always valid in append mode.
     */
    public int fit(long timestamp, boolean append) {

        int offset = (int) ((timestamp / this.resolution - this.startTimestamp / this.resolution));

        if (offset < 0) {
            // Specified timestamp is earlier the the start timestamp of current queue, which is considered an invalid
            // request.
            return -1;

        } else if (offset >= 2 * size - 1) {
            // If not in append mode, returns -1 indicating an illegal query.
            if (!append) {
                return -1;
            }

            // All old records are expired. To fully exploit the duration window, cleans the queue and sets the
            // beginning to the time slot of query timestamp
            LOG.debug("clean queue, current counter start timestamp is {}, enqueue timestamp is {}.",
                    this.startTimestamp, timestamp);
            clean();
            this.startTimestamp = timestamp / this.resolution * this.resolution;
            return this.startIndex;

        } else if (offset >= size) {
            // If not in append mode, returns -1 indicating an illegal query.
            if (!append) {
                return -1;
            }

            // Some records are still valid. Discards expired ones by adjusting the start index in a looped-queue
            // manner, and updates start timestamp.
            for (int i = this.size; i <= offset; i++) {
                int index = (this.startIndex + i) % this.size;
                this.list.set(index, null);
            }
            this.startTimestamp += (offset - this.size + 1) * this.resolution;
            this.startIndex = (this.startIndex + offset + 1) % this.size;
            offset--;
        }
        return (this.startIndex + offset) % this.size;
    }

    /**
     * Gets the record in the slot which encloses the specified timestamps.
     * 
     * @param timestamp
     *            Timestamp of corresponding record to return
     * @return The record corresponding to the timestamp, which will be <code>null</code> if the timestamps exceeds time
     *         window of the queue.
     */
    public T get(long timestamp) {
        int index = fit(timestamp);
        return get(index);
    }

    /**
     * Gets the records at the specified index.
     * 
     * @param index
     *            Index of corresponding record to return
     * @return The record at <code>index</code>, which will be <code>null</code> if the index is not in the valid range.
     */
    public T get(int index) {
        return (index >= 0 && index < this.list.size()) ? this.list.get(index) : null;
    }

    /**
     * Adds record to the end of queue, automatically adjust the time duration window and start timestamp to enclose new
     * timestamp.
     * 
     * @param timestamp
     *            Timestamp of new record
     * @param e
     *            New record to add
     * @return True if insertion succeeded.
     */
    public boolean enqueue(long timestamp, T e) {
        return set(timestamp, e, true);
    }

    /**
     * Sets the record at specified index to a new one.
     * 
     * @param timestamp
     *            Timestamp of new record
     * @param e
     *            New record to update
     * @return True if update succeeded.
     */
    public boolean set(long timestamp, T e) {
        return set(timestamp, e, false);
    }

    private boolean set(long timestamp, T e, boolean append) {
        int index = fit(timestamp, append);
        if (index == -1) {
            return false;
        }
        this.list.set(index, e);
        return true;
    }

    /**
     * Gets start timestamp.
     * 
     * @return Start timestamp.
     */
    public long getStartTs() {
        return this.startTimestamp;
    }

    /**
     * Gets end timestamp, which is equivalent to <code>startTimestamp + resolution * size</code>.
     * 
     * @return End timestamp.
     */
    public long getEndTs() {
        return this.startTimestamp + this.resolution * this.size;
    }

    /**
     * Gets the time duration of slots, a.k.a. the resolution, of the queue.
     * 
     * @return Resolution of queue slots, in seconds.
     */
    public int getResolution() {
        return this.resolution;
    }

    /**
     * Gets the start index of queue.
     * 
     * @return Start index.
     */
    public int getStartIndex() {
        return this.startIndex;
    }

    /**
     * Moves forward the duration window to the one starting at the slot containing the specified timestamp.
     * 
     * @param timestamp
     *            Timestamp indicating the new start timestamp of the duration window.
     * @return True if the given timestamp is valid, i.e., later than the original start timestamp.
     */
    public boolean moveForward(long timestamp) {

        timestamp = timestamp / this.resolution * this.resolution;

        if (this.startTimestamp >= timestamp) {
            return false;
        }
        int gap = (int) ((timestamp / this.resolution - this.startTimestamp / this.resolution));
        if (gap >= size) {

            // Discards all old records if new start timestamp is later than the end time of old duration.
            clean();
        } else {

            // Clears expired records, and updates start timestamp, and start index in a looped-queue manner.
            for (int i = 0; i < gap; i++) {
                int index = (this.startIndex + i) % this.size;
                this.list.set(index, null);
            }
            this.startIndex = (this.startIndex + gap) % this.size;
        }
        this.startTimestamp = timestamp;
        return true;
    }

    private void clean() {
        this.list.clear();
        for (int i = 0; i < this.size; i++) {
            this.list.add(null);
        }
    }
}
