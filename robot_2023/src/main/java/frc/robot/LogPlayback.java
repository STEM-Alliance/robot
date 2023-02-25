package frc.robot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.stream.Stream;

public class LogPlayback {
    int m_totalSamples = 0;
    int m_simIndex = 0;
    double m_samples[][];

    public LogPlayback(String logfile) {
        /* Load logged data */
        try {
            BufferedReader br = new BufferedReader(new FileReader(logfile));
            var lines = br.lines().toList();
            br.close();

            m_simIndex = 0;
            for (String line : lines)
            {
                if (line.contains("FullData"))
                {
                    m_totalSamples++;
                }
            }

            m_samples = new double[m_totalSamples][5];

            for (String line : lines)
            {
                if (line.contains("FullData"))
                {
                    String ele[] = line.split(",");
                    String data = ele[2];
                    data = data.replace("[", "");
                    data = data.replace("]", "");
                    ele = data.split(";");
                    m_samples[m_simIndex][0] = Double.parseDouble(ele[0]);
                    m_samples[m_simIndex][1] = Double.parseDouble(ele[1]);
                    m_samples[m_simIndex][2] = Double.parseDouble(ele[2]);
                    m_samples[m_simIndex][3] = Double.parseDouble(ele[3]);
                    m_samples[m_simIndex][4] = Double.parseDouble(ele[20]);
                    m_simIndex++;
                }
            }
            m_simIndex = 0;

        } catch (Exception e) {
            System.out.println("Failed to load log file");
        }
    }

    public double getLPos()
    {
        if (m_samples != null)
            return m_samples[m_simIndex][0];
        return 0;
    }

    public double getRPos()
    {
        if (m_samples != null)
            return m_samples[m_simIndex][1];
        return 0;
    }

    public double getLVel()
    {
        if (m_samples != null)
            return m_samples[m_simIndex][2];
        return 0;
    }

    public double getRVel()
    {
        if (m_samples != null)
            return m_samples[m_simIndex][3];
        return 0;
    }

    public double getHeading()
    {
        if (m_samples != null)
            return m_samples[m_simIndex][4];
        return 0;
    }

    public void stepSim()
    {
        if (m_simIndex < m_totalSamples)
            m_simIndex++;
    }
}
