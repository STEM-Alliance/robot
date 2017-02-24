package org.wfrobotics;

public class LookupTable {
    
    double[] m_Input;
    double[] m_Output;
    
    int size;

    /**
     * initialize a table lookup object
     * @param in_array
     * @param out_array
     */
    public LookupTable(double[] in_array, double[] out_array)
    {
        size = Math.min(in_array.length, out_array.length);
        
        m_Input = new double[size];
        m_Output = new double[size];
        
        for (int i = 0; i < size; i++)
        {
            m_Input[i] = in_array[i];
            m_Output[i] = out_array[i];
        }
    }
    
    /**
     * Find the closest index, rounded down, of the input axis
     * @param x
     * @return
     */
    private int indexSearch(double x)
    {
        int Min_Index;
        int Mid_Index;
        int Max_Index;
        
        Min_Index = 0;
        Max_Index = size;

        while (Min_Index < Max_Index)
        {
            Mid_Index = (Min_Index + Max_Index) >> 1;
            if (x > m_Input[Mid_Index])
            {
                Min_Index = Mid_Index + 1;
            }
            else
            {
                Max_Index = Mid_Index;
            }
        }
        return(Min_Index);
    }
    
    /**
     * Get the linear interpolated value for a given input
     * @param x
     * @return
     */
    public double get(double x)
    {
        double result = 0;
        int minIndex = 0;
        double valDiff = 0;
        double indexValDiff = 0;
        double ratioX = 0;
        
        if(x <= m_Input[0])
        {
            // minimum limit
            result = m_Output[0];
        }
        else if(x >= m_Input[size - 1])
        {
            // maximum limit
            result = m_Output[size - 1];
        }
        else
        {
            // find within limits
            minIndex = indexSearch(x);
            
            if(x == m_Input[minIndex])
            {
                // found direct match
                result = m_Output[minIndex];
            }
            else
            {
                // interpolation needed
                valDiff = (x - m_Input[minIndex - 1]);
                indexValDiff = (m_Input[minIndex] - m_Input[minIndex - 1]);
                
                if( indexValDiff != 0 )
                {
                    ratioX = valDiff / indexValDiff;
                }
                else
                {
                    // Divide by zero
                }
                
                result = m_Output[minIndex - 1] + ((m_Output[minIndex] - 
                         m_Output[minIndex - 1]) * (ratioX));
            }
        }
        
        return result;
    }
}
