package com.pduvvur.saveme.guardian;

/**
 * Created by PradeepKumar on 7/12/2015.
 */
public class Guardian
{
    private String m_name;
    private String m_phoneNumber;

    public Guardian(String name, String number)
    {
        m_name = name;
        m_phoneNumber = number;
    }

    public String getName()
    {
        return m_name;
    }

    public String getPhoneNumber()
    {
        return m_phoneNumber;
    }

    @Override
    public String toString()
    {
        return  m_name + " " + m_phoneNumber;
    }
}
