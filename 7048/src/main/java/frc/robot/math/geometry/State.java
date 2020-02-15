package frc.robot.math.geometry;

import frc.robot.math.CSVWritable;
import frc.robot.math.interpolation.Interpolable;

public interface State<S> extends Interpolable<S>, CSVWritable {
    double distance(final S other);

    boolean equals(final Object other);

    String toString();

    String toCSV();
}
