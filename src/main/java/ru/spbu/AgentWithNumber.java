package ru.spbu;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.*;

public class AgentWithNumber extends Agent {
    private String[] linkedAgents;
    private Integer id, number, graphSize;
    private Map<Integer, Integer> idToNumber = new HashMap<>();

    @Override
    protected void setup() {
        this.id = Integer.parseInt(getAID().getLocalName());
        this.number = new Random().nextInt(20);
        idToNumber.put(id, number);

        Object[] args = getArguments();
        linkedAgents = new String[args.length - 1];
        for (int i = 0; i < args.length - 1; i++) {
            linkedAgents[i] = (String) args[i];
        }
        graphSize = Integer.parseInt(String.valueOf(args[args.length - 1]));
        System.out.println("Agent #" + id + " with num = " + number + " and with neighbours: " + String.join(" ", linkedAgents));

        addBehaviour(new Send());
        addBehaviour(new Consume());
    }

    class Consume extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
            if (msg != null) {
                int lastSize = idToNumber.size();
                String sender = msg.getSender().getName().split("@")[0];

                String content = msg.getContent();
                StringTokenizer pairs = new StringTokenizer(content, ";");
                while (pairs.hasMoreTokens()) {
                    StringTokenizer pair = new StringTokenizer(pairs.nextToken());
                    Integer id = Integer.valueOf(pair.nextToken());
                    Integer number = Integer.valueOf(pair.nextToken());
                    idToNumber.put(id, number);
                }
                System.out.println("Agent #" + id + " got " + content + " from " + sender);

                if (idToNumber.size() != lastSize) {
                    myAgent.addBehaviour(new Send());
                }

                if (graphSize == idToNumber.size()){
                    //if agent id is the less -> count avr
                    int minId = idToNumber.keySet().stream().min(Comparator.naturalOrder()).get();
                    if (minId == id) {
                        System.out.println("AVR = " + idToNumber.values().stream().mapToDouble(value -> value).average().getAsDouble());
                        myAgent.doDelete();
                    }
                    myAgent.removeBehaviour(this);
                }

            } else {
                block();
            }
        }
    }

    class Send extends OneShotBehaviour {
        public void action() {
            ACLMessage inform = new ACLMessage(ACLMessage.INFORM);
            for (String neighbour : linkedAgents) {
                    inform.addReceiver(new AID(neighbour, AID.ISLOCALNAME));
            }

            if (inform.getAllReceiver().hasNext()) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<Integer, Integer> entry : idToNumber.entrySet()) {
                    sb.append(entry.getKey()).append(" ").append(entry.getValue()).append(";");
                }
                inform.setContent(sb.toString());

                System.out.println("Agent #" + id + " sent " + sb);
                myAgent.send(inform);
            }
        }
    }
}
