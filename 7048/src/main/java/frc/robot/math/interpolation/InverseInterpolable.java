package frc.robot.math.interpolation;

/**
 * Given two numbers, <b>find a percentage in between the two numbers</b> that a third number is (ex: 4.5 is .75 in between 3 & 5 )
 *
 * InverseInterpolable is an interface used by an Interpolating Tree as the Key type. Given two endpoint keys and a
 * third query key, an InverseInterpolable object can calculate the interpolation parameter of the query key on the
 * interval [0, 1].
 *
 * @param <T>
 *            The Type of InverseInterpolable
 * @see InterpolatingTreeMap
 * @author Code: Team 254<br>Documentation: STEM Alliance of Fargo Moorhead
 */
public interface InverseInterpolable<T> {
    /**
     * Given this point (lower), a query point (query), and an upper point (upper), estimate how far (on [0, 1]) between
     * 'lower' and 'upper' the query point lies.
     *
     * @param upper
     * @param query
     * @return The interpolation parameter on [0, 1] representing how far between this point and the upper point the
     *         query point lies.
     */
    public double inverseInterpolate(T upper, T query);
}
