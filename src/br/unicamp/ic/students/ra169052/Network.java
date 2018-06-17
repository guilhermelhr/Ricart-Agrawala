package br.unicamp.ic.students.ra169052;

import java.util.ArrayList;
import java.util.LinkedList;

public class Network {
    private ArrayList<Process> peers = new ArrayList<>();
    private ArrayList<LinkedList<Message>> messageQueues = new ArrayList<>();

    /**
     * Adds a peer to the network
     * @param process
     */
    public void addPeer(Process process){
        process.network = this;
        process.clock = new Clock(peers.size(), 0);
        messageQueues.add(new LinkedList<>());
        peers.add(process);
    }

    /**
     * Sends a message to specified process
     * @param message
     * @param pid destination identifier
     */
    public void sendTo(Message message, int pid){
        LinkedList<Message> messageQueue = messageQueues.get(pid);
        synchronized (messageQueue){
            messageQueue.add(message);
        }
    }

    /**
     * Gets message for a process
     * @param pid replica's pid
     * @return oldest message if there is one, null otherwise
     */
    public Message getMessageFor(int pid) {
        LinkedList<Message> messageQueue = messageQueues.get(pid);
        synchronized (messageQueue) {
            if (!messageQueue.isEmpty()) {
                return messageQueue.removeFirst();
            }else{
                return null;
            }
        }
    }

    /**
     * Sends message to every peer on the network
     * @param message
     */
    public synchronized void broadcast(Message message) {
        for(LinkedList<Message> messageQueue : messageQueues){
            messageQueue.add(message);
        }
    }

    /**
     * Gets number of peers on the network
     * @return peer count
     */
    public int getPeerCount(){
        return peers.size();
    }
}