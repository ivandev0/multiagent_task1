package ru.spbu;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import java.util.*;

public class App {
    /*private static Map<Integer, List<String>> graph = new HashMap<Integer, List<String>>() {{
        put(1, listOf("2"));
        put(2, listOf("1", "3"));
        put(3, listOf("2", "4"));
        put(4, listOf("3", "5"));
        put(5, listOf("4"));
    }};*/

    /*private static Map<Integer, List<String>> graph = new HashMap<Integer, List<String>>() {{
        put(1, listOf("2"));
        put(2, listOf("1", "3"));
        put(3, listOf("2"));
    }};*/

    private static Map<Integer, List<String>> graph = new HashMap<Integer, List<String>>() {{
        put(1, listOf("2", "10"));
        put(2, listOf("1", "3"));
        put(3, listOf("2", "4"));
        put(4, listOf("3", "5"));
        put(5, listOf("4", "6"));
        put(6, listOf("5", "7"));
        put(7, listOf("6", "8"));
        put(8, listOf("7", "9"));
        put(9, listOf("8", "10"));
        put(10, listOf("9", "1"));
    }};



    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        //Create a container to host the Default Agent
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.MAIN_PORT, "10098");
        p.setParameter(Profile.GUI, "true");
        ContainerController cc = rt.createMainContainer(p);

        try {
            for (int i = 1; i <= graph.size(); i++) {
                AgentController agent = cc.createNewAgent(
                        Integer.toString(i), AgentWithNumber.class.getName(), toArgs(graph.size(), graph.get(i))
                );
                agent.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> listOf(String... args) {
        return new ArrayList<>(Arrays.asList(args));
    }

    private static String[] toArray(List<String> args) {
        return args.toArray(new String[0]);
    }

    private static String[] toArgs(Integer nodesCount, List<String> neighbours) {
        neighbours.add(String.valueOf(nodesCount));
        return toArray(neighbours);
    }
}
