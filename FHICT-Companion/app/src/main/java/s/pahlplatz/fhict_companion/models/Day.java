package s.pahlplatz.fhict_companion.models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Stefan on 4-12-2016.
 * <p>
 * Class that represents a day in the schedule.
 */

public class Day implements java.io.Serializable {
    private Date date;
    private ArrayList<Block> blocks;

    public Day(final Date date) {
        this.date = date;
        blocks = new ArrayList<>();
    }

    /**
     * Returns the day corresponding to the index.
     *
     * @param i index of the block.
     * @return Block object.
     */
    public Block getBlock(final int i) {
        return blocks.get(i);
    }

    /**
     * Returns the amount of blocks in the day.
     */
    public int size() {
        return blocks.size();
    }

    /**
     * Returns the Date of the day.
     */
    Date getDate() {
        return date;
    }

    /**
     * Adds the given block to the day.
     */
    public void addBlock(final Block block) {
        for (int i = 0; i < blocks.size(); i++) {
            int startHour = Integer.parseInt(blocks.get(i).getStart().substring(0, 2));
            int startHour2 = Integer.parseInt(block.getStart().substring(0, 2));

            if (startHour > startHour2) {
                blocks.add(i, block);
                return;
            } else if (startHour == startHour2) {
                int startMin = Integer.parseInt(blocks.get(i).getStart().substring(3, 5));
                int startMin2 = Integer.parseInt(block.getStart().substring(3, 5));

                if (startMin > startMin2) {
                    blocks.add(i, block);
                    return;
                }
            }
        }
        blocks.add(block);
    }

    /**
     * Merges two consecutive blocks if the times follow up and the subjects are the same.
     */
    void mergeDuplicates() {
        for (int i = 0; i < blocks.size() - 1; i++) {
            Block current = blocks.get(i);
            Block next = blocks.get(i + 1);

            // Merge two consecutive blocks.
            if (current.getSubject().equals(next.getSubject())
                    && current.getEnd().equals(next.getStart())) {
                current.setEnd(next.getEnd());
                blocks.remove(i + 1);
                i = 0;
            }
            // Merge same blocks in different rooms.
            else if (current.getSubject().equals(next.getSubject()) &&
                    current.getStart().equals(next.getStart()) &&
                    current.getEnd().equals(next.getEnd())) {
                current.setRoom(current.getRoom() + " / " + next.getRoom());
                blocks.remove(i + 1);
                i = 0;
            }
        }
    }

    /**
     * Inserts breaks where the end and start of different blocks do not line up.
     */
    void addBreaks() {
        for (int i = 0; i < blocks.size(); i++) {
            if (i + 1 < blocks.size()) {
                if (!blocks.get(i).getEnd().equals(blocks.get(i + 1).getStart())
                        && !blocks.get(i).getSubject().equals("zelfwerk")
                        && !blocks.get(i).getStart().equals(blocks.get(i + 1).getStart())) {
                    blocks.add(i + 1, new Block(blocks.get(i).getEnd(), blocks.get(i + 1).getStart()));
                    i = 0;
                }
            }
        }
    }

    /**
     * Disposes of the day.
     */
    void dispose() {
        date = null;
        blocks.clear();
        blocks = null;
    }

    @Override
    public String toString() {
        String retString = date.toString() + "\n\t\t";
        for (Block item : blocks) {
            retString += item.toString() + "\n\t\t";
        }
        return retString;
    }
}
