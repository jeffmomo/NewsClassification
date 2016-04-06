package Scraper;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mdl94 on 20/09/2015.
 */
public class NewsDocument extends Object implements Serializable {

    private String _src;
    private String _heading;
    private String _section;
    private String _text;
    private int _shares;
    private Date _datePosted;
    private Date _dateScraped;
    private int _version;

    public NewsDocument(String src, String heading, String section, String text, int shares, Date posted, Date dateScraped)
    {
        _heading = heading;
        _section = section;
        _text = text;
        _shares = shares;
        _datePosted = posted;
        _dateScraped = dateScraped;
        _src = src;
    }

    public String getSource()
    {
        return _src;
    }

    public String getHeading()
    {
        return _heading;
    }

    public String getSection()
    {
        return _section;
    }

    public Date getDatePosted()
    {
        return _datePosted;
    }

    public long getAge()
    {
        return _dateScraped.getTime() - _datePosted.getTime();
    }

    public String getText()
    {
        return _text;
    }

    public int getShares()
    {
        return _shares;
    }

    @Override
    public String toString()
    {
        return "Heading: " + _heading + "\nSection: " + _section + "\nShares: " + _shares + "\nPublished: " + _datePosted + "\nContent: \n" + _text + "\n";
    }



}
