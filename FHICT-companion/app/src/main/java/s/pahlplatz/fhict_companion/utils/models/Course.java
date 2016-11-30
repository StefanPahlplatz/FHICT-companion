package s.pahlplatz.fhict_companion.utils.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Stefan on 30-11-2016.
 */

public class Course implements Serializable
{
    private long id;
    private String name;
    private String descr;

    private ArrayList<ItemDetail> itemList = new ArrayList<ItemDetail>();

    public Course(long id, String name, String descr)
    {
        this.id = id;
        this.name = name;
        this.descr = descr;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescr()
    {
        return descr;
    }

    public void setDescr(String descr)
    {
        this.descr = descr;
    }

    public ArrayList<ItemDetail> getItemList()
    {
        return itemList;
    }

    public void setItemList(ArrayList<ItemDetail> itemList)
    {
        this.itemList = itemList;
    }
}
