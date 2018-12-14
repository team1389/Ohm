package com.team1389.util.bezier;

public class BezierCurve {

	public static final BezierCurve LINEAR;

	public static final BezierCurve EASE;

	public static final BezierCurve EASE_IN;

	public static final BezierCurve EASE_IN_OUT;

	public static final BezierCurve EASE_OUT;
	public static final BezierPoint zero = new BezierPoint(0.0, 0.0);
	public static final BezierPoint one = new BezierPoint(1.0, 1.0);

	static {

		final BezierPoint a = new BezierPoint(0.25, 0.1);
		final BezierPoint b = new BezierPoint(0.25, 1.0);
		final BezierPoint c = new BezierPoint(0.42, 0.0);
		final BezierPoint d = new BezierPoint(0.58, 1.0);

		LINEAR = new BezierCurve(zero, one);
		EASE = new BezierCurve(zero, a, b, one);
		EASE_IN = new BezierCurve(zero, c, one, one);
		EASE_IN_OUT = new BezierCurve(zero, c, d, one);
		EASE_OUT = new BezierCurve(zero, zero, d, one);
	}

	private final BezierPoint[] p;

	public BezierCurve(BezierPoint... p) {
		if (p.length < 2)
			throw new IllegalArgumentException("Minimun number of interpolation points is 2, you have: " + p.length);

		this.p = p;

	}

	public BezierCurve(double cp1x, double cp1y, double cp2x, double cp2y) {
		this(BezierCurve.zero, new BezierPoint(cp1x, cp1y), new BezierPoint(cp2x, cp2y), BezierCurve.one);
	}

	public BezierPoint getPoint(double t) {
		return BezierCurve.getPoint(t, this.p);
	}

	public static BezierPoint getPoint(double t, BezierPoint... p) {
		final double x[] = new double[p.length];
		final double y[] = new double[p.length];

		for (int i = 0; i < p.length; i++) {
			x[i] = p[i].getX();
			y[i] = p[i].getY();
		}

		x[0] = getPoint(t, x);
		y[0] = getPoint(t, y);

		return new BezierPoint(x[0], y[0]);
	}

	public static double getPoint(double t, double... p) {
		double oldT = t;
		if (p.length == 2)
			return p[0] + (p[1] - p[0]) * t;

		if (0 > t || t > 1)
			t = -t;
		if (p.length < 2)
			throw new IllegalArgumentException("Minimum number of interpolation points is 2, you have: " + p.length);

		final double[] d = new double[p.length];
		for (int i = 0; i < d.length; i++)
			d[i] = p[i];

		for (int i = d.length; i > 0; i--)
			for (int j = 1; j < i; j++)
				d[j - 1] = getPoint(t, d[j - 1], d[j]);

		if (oldT < 0) {
			return -d[0];
		} else {
			return d[0];
		}

	}

}
