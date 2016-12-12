package csi5308.assignment3.simulation.algorithms;

import csi5308.assignment3.simulation.SimulationEvent;
import csi5308.assignment3.simulation.Time;

import java.util.Collection;

public interface AlgorithmicController {

    Collection<SimulationEvent> step(Time t);
    @Override
    String toString();
    boolean done();
    String reducedString();
}
