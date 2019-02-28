package com.team1389.controllers;

import com.team1389.command_framework.CommandUtil;
import com.team1389.command_framework.command_base.Command;
import com.team1389.configuration.PIDConstants;
import com.team1389.configuration.PIDInput;
import com.team1389.hardware.inputs.interfaces.BinaryInput;
import com.team1389.hardware.inputs.software.RangeIn;
import com.team1389.hardware.outputs.software.RangeOut;
import com.team1389.hardware.value_types.PIDTunableValue;
import com.team1389.hardware.value_types.Value;
import com.team1389.watch.CompositeWatchable;
import com.team1389.watch.input.listener.NumberInput;

/**
 * This class applies PID control to a set of input/output streams <br>
 * Does all computation synchronously (i.e. the calculate() function must be
 * called by the user from his own thread)
 * 
 * @author amind
 *
 * @param <O>
 *                the value type of the output stream
 * @param <I>
 *                the value type of the input stream
 */
public class SynchronousPIDController<O extends Value, I extends PIDTunableValue> extends SynchronousPID
{
	protected RangeOut<O> output;
	protected RangeIn<I> source;
	protected boolean enabled;

	/**
	 * @param kP
	 *                   the proportional gain of the PID controller
	 * @param kI
	 *                   the integral gain of the PID controller
	 * @param kD
	 *                   the derivative gain of the PID controller
	 * @param source
	 *                   the input stream
	 * @param output
	 *                   the output stream
	 */
	public SynchronousPIDController(double kP, double kI, double kD, double kF, RangeIn<I> source, RangeOut<O> output)
	{
		super(kP, kI, kD, kF, PIDRangeIn.checkSourceType(source));
		System.out.println(source.get());
		this.source = source.copy();
		this.output = output;
		enable();
		setInputRange(source.min(), source.max());
		setOutputRange(output.min(), output.max());
	}

	/**
	 * @param constants
	 *                      a set of gains for the PID controller
	 * @param source
	 *                      the input stream
	 * @param output
	 *                      the output stream
	 */
	public SynchronousPIDController(PIDConstants constants, RangeIn<I> source, RangeOut<O> output)
	{
		this(constants.p, constants.i, constants.d, constants.f, source, output);
	}

	/**
	 * updates the PID controller with the value of the input stream, applies
	 * the output of the controller to the output stream
	 */
	public void update()
	{
		if (enabled)
		{
			output.set(calculate(source.get()));
		}
		else
		{
			output.set(0.0);
		}
	}

	public void setEnabled(boolean val)
	{
		enabled = val;
	}

	public void enable()
	{
		enabled = true;
	}

	public void disable()
	{
		enabled = false;
	}

	/**
	 * @return an output stream that will pass applied values to the PID
	 *         controller as setpoints
	 */
	public RangeOut<I> getSetpointSetter()
	{
		return new RangeOut<I>(this::setSetpoint, source.min(), source.max());
	}

	/**
	 * @return the input stream
	 */
	public RangeIn<I> getSource()
	{
		return source;
	}

	/**
	 * @return the original output stream
	 */
	public RangeOut<O> getOutput()
	{
		return output;
	}

	/**
	 * @return an {@link Command} that updates the PID controller indefinitely
	 */
	public Command getPIDToCommand()
	{
		return getPIDToCommand(() ->
		{
			return false;
		});
	}

	/**
	 * @param tolerance
	 *                      the tolerance within which to halt the PID updates
	 * @return an {@link Command} that updates the PID controller until the
	 *         value of the input stream is within the given tolerance of the
	 *         setpoint
	 */
	public Command getPIDToCommand(double tolerance)
	{
		return getPIDToCommand(() ->
		{
			return onTargetStable(tolerance);
		});
	}

	public Command getPIDToCommand(double setpoint, double tolerance)
	{
		this.setSetpoint(setpoint);
		return getPIDToCommand(() ->
		{
			return onTargetStable(tolerance);
		});
	}

	/**
	 * @param exitCondition
	 *                          boolean stream that determines when to halt PID
	 *                          updates
	 * @return an {@link Command} that updates the PID controller until the
	 *         value of the boolean stream is true
	 */
	public Command getPIDToCommand(BinaryInput exitCondition)
	{
		return new Command()
		{
			@Override
			protected boolean execute()
			{
				update();
				return exitCondition.get();
			}

			@Override
			protected void done()
			{
				output.set(0);
			}
		};
	}

	/**
	 * @param constants
	 *                      a set of gains for the PID controller
	 */
	public void setPID(PIDConstants constants)
	{
		super.setPID(constants.p, constants.i, constants.d, constants.f);
	}

	/**
	 * @return the current gains of the PID controller
	 */
	public PIDConstants getPID()
	{
		return new PIDConstants(getP(), getI(), getD());
	}

	/**
	 * @param name
	 *                 the name of the tuner watchable
	 * @return a watchable object that can accept input to adjust the gains of
	 *         the PID controller TODO this can be all the time once feedforward
	 *         is implemented
	 */
	public CompositeWatchable getPIDTuner(String name)
	{
		return CompositeWatchable.of(name,
				new PIDInput(name, getPID(), true, this::setPID).getSubWatchables(CompositeWatchable.makeStem()));
	}

	public CompositeWatchable getPIDTunerWithSetpoint(String name)
	{
		return CompositeWatchable.of(name,
				new PIDInput(name, getPID(), true, this::setPID).getSubWatchables(CompositeWatchable.makeStem())
						.put(new NumberInput("setpoint", getSetpoint(), this::setSetpoint)));
	}

}
