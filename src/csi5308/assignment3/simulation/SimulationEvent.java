package csi5308.assignment3.simulation;

import csi5308.assignment3.simulation.algorithms.AlgorithmicController;

public class SimulationEvent {

    private final Time t;
    private final AlgorithmicController controller;

    public SimulationEvent(Time t, AlgorithmicController controller) {
        this.t = t;
        this.controller = controller;
    }

    public Time getTimeOfEvent() {
        return this.t;
    }

    public AlgorithmicController getSubject() {
        return this.controller;
    }
}
