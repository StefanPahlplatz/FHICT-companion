package s.pahlplatz.fhict_companion.utils.models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Stefan on 4-12-2016.
 * <p>
 * Class that represents a day in the schedule
 */

public class Day
{
    private Date date;
    private ArrayList<Block> blocks;

    public Day(Date date)
    {
        this.date = date;
        blocks = new ArrayList<>();
    }

    /**
     * Returns the day corresponding to the index
     *
     * @param i index of the block
     * @return Block object
     */
    public Block getBlock(int i)
    {
        return blocks.get(i);
    }

    /**
     * Returns the amount of blocks in the day
     *
     * @return integer
     */
    public int size()
    {
        return blocks.size();
    }

    /**
     * Returns the Date of the day
     *
     * @return Date object
     */
    Date getDate()
    {
        return date;
    }

    /**
     * Adds the given block to the day
     *
     * @param block Block
     */
    void addBlock(Block block)
    {
        blocks.add(block);
    }

    /**
     * Merges consecutive 2 blocks if the times follow up and the subjects are the same
     */
    void mergeDuplicates()
    {
        for (int i = 0; i < blocks.size(); i++)
        {
            if (i + 1 < blocks.size())
            {
                if (blocks.get(i).getSubject().equals(blocks.get(i + 1).getSubject()) &&
                        blocks.get(i).getEnd().equals(blocks.get(i + 1).getStart()))
                {
                    blocks.get(i).setEnd(blocks.get(i + 1).getEnd());
                    blocks.remove(i + 1);
                    i = 0;
                }
            }
        }
    }

    /**
     * Inserts breaks where the end and start of different blocks do not line up
     */
    void addBreaks()
    {
        for (int i = 0; i < blocks.size(); i++)
            if (i + 1 < blocks.size())
                if (!blocks.get(i).getEnd().equals(blocks.get(i + 1).getStart()) &&
                        !blocks.get(i).getSubject().equals("zelfwerk"))
                {
                    blocks.add(i + 1, new Block(blocks.get(i).getEnd(), blocks.get(i + 1).getStart()));
                    i = 0;
                }
    }

    /**
     * Disposes of the day
     */
    void dispose()
    {
        date = null;
        blocks.clear();
        blocks = null;
    }

    @Override
    public String toString()
    {
        String retString = date.toString() + "\n\t\t";
        for (Block item : blocks)
        {
            retString += item.toString() + "\n\t\t";
        }
        return retString;
    }
}
