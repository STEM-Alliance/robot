package org.wfrobotics.prototype.subsystems;

/** Provides raw path segments data */
public interface PathSource
{
    /** Path segment - trajectory point inputs as [index][side][position/velocity] */
    double[][][] get(int start, int length);
    /** Number of path segments */
    int length();
}