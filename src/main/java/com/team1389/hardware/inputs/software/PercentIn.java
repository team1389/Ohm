package com.team1389.hardware.inputs.software;

import com.team1389.hardware.inputs.interfaces.ScalarInput;
import com.team1389.hardware.value_types.Percent;
import com.team1389.util.bezier.BezierCurve;

/**
 * An input stream with range -1 to 1
 * 
 * @author Jacob Prinz
 */
public class PercentIn extends RangeIn<Percent>
{
	/**
	 * @param input
	 *                  the input stream
	 */
	public PercentIn(ScalarInput<Percent> input)
	{
		super(Percent.class, input, -1, 1);
	}

	/**
	 * @param in
	 *               the stream to convert to a percent stream
	 */
	public PercentIn(RangeIn<?> in)
	{
		this(ScalarInput.mapToPercent(in.input, in.min, in.max));
		this.operations = in.operations;
		addOperation(d -> "-> map from [" + in.min + "," + in.max + "] to [" + -1 + "," + 1 + "] = " + d);
	}

	public PercentIn applyBezier(BezierCurve curve)
	{
		PercentIn newStream = new PercentIn(super.getMapped(d -> curve.getPoint(d).getY()).input);
		newStream.addOperation(d -> "-> bezier = " + d);
		return newStream;
	}

	public void run()
	{

	}

	@Override
	public PercentIn copy()
	{
		return new PercentIn(input);
	}

}
