package csi5308.assignment3.simulation;

import csi5308.assignment3.components.Graph;
import csi5308.assignment3.components.communication.Mailman;
import csi5308.assignment3.simulation.algorithms.AlgorithmicController;
import csi5308.assignment3.simulation.algorithms.YoYo.NodeController;
import csi5308.assignment3.simulation.topologies.GraphGenerator;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Simulator {

    private static final long CLOCK_TICKS = TimeUnit.MILLISECONDS.toMillis(0L);
    private static final long START_TIME = 0;
    private static final int NUM_NODES = 16*4*4;
    private static final int sampleSize = 100;
    private static final Random numGenerator = new Random();
    private static final List<Supplier<Graph>> SUPPLIERS = topologies();
    private static final Supplier<Long> messageLatency = () -> (long)numGenerator.nextInt(4) + 1;

    private static List<Supplier<Graph>> topologies() {
        List<Supplier<Graph>> list = new LinkedList<>();
        list.add(() -> GraphGenerator.generateCompleteGraph(NUM_NODES));
        list.add(() -> GraphGenerator.generateRingGraph(NUM_NODES));
        list.add(() -> GraphGenerator.generateRandomGraph(NUM_NODES, () -> numGenerator.nextDouble() < 0.25));
        list.add(() -> GraphGenerator.generateRandomGraph(NUM_NODES, () -> numGenerator.nextDouble() < 0.5));
        list.add(() -> GraphGenerator.generateRandomGraph(NUM_NODES, () -> numGenerator.nextDouble() < 0.75));
        list.add(() -> GraphGenerator.generateHyperCube((int) (Math.log(NUM_NODES) / Math.log(2))));
        list.add(() -> GraphGenerator.generateTorus((int)Math.sqrt(NUM_NODES)));
        list.add(() -> GraphGenerator.generateMesh(((int)Math.sqrt(NUM_NODES))));
        return list;
    }

    public static void main(String[] args) {
        for (int cnt = 0; cnt < SUPPLIERS.size(); cnt += 1) {
            Supplier<Graph> gSupplier = SUPPLIERS.get(cnt);
            List<Long> time = new LinkedList<>();
            List<Long> messages = new LinkedList<>();

            for (int i = 0; i < sampleSize; i += 1) {
                Graph g = gSupplier.get();
                // System.out.println(g.toString());
                TimeController controller = new TimeController(START_TIME, CLOCK_TICKS);
                Mailman mailman = new Mailman(g, messageLatency);
                long distributed = 0;
                List<AlgorithmicController> controllers = g.getNodes().stream()
                        .map(NodeController::new)
                        .collect(Collectors.toList());

                List<SimulationEvent> events = controllers.stream()
                        .map(c -> new SimulationEvent(new Time(START_TIME), c))
                        .collect(Collectors.toList());
                List<MessageEvent> messageQueue = new LinkedList<>();

                for (Time t : controller) {

                    //System.out.println("\n--------------------- Time " + t.getTime() + "---------------------");

                    Collection<MessageEvent> temp = new LinkedList<>();
                    messageQueue.stream()
                            .filter(m -> m.getTime().equals(t))
                            .map(m -> {temp.add(m); return m;})
                            .map(MessageEvent::getMessage)
                            .forEach(m -> m.getDirection().getArrival().getMailbox().depositMail(m));
                    messageQueue.removeAll(temp);

                    if (messageQueue.stream().anyMatch(m -> m.getTime().getTime() <= t.getTime())) {
                        throw new IllegalStateException();
                    }

                    List<SimulationEvent> toProcess = events.stream()
                            .filter(e -> e.getTimeOfEvent().equals(t))
                            .collect(Collectors.toList());

                    events.removeAll(toProcess);
                    toProcess.stream().flatMap(e -> e.getSubject().step(t).stream()).forEach(events::add);

                    //StringJoiner joiner = new StringJoiner(" ~ ");
                    //controllers.stream().map(AlgorithmicController::reducedString).forEach(joiner::add);
                    //System.out.println(joiner.toString());
                    Collection<MessageEvent> newMessages = mailman.distributeMail(t);
                    distributed += newMessages.size();
                    messageQueue.addAll(newMessages);

                    if (controllers.stream().allMatch(AlgorithmicController::done)) {
                        time.add(t.getTime());
                        messages.add(distributed);
                        System.out.print("|");
                        break;
                    }
                }
            }

            System.out.println("\n\nTopology " + cnt);
            System.out.println("Sample Size: " + messages.size());
            System.out.println("Nodes: " + NUM_NODES);
            System.out.println("--- Messages ---");
            System.out.println(new Statistics(messages.stream().map(d -> (double) d).collect(Collectors.toList())));
            System.out.println("--- Time ---");
            System.out.println(new Statistics(time.stream().map(d -> (double) d).collect(Collectors.toList())));
        }
    }


}
