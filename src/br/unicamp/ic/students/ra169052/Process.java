package br.unicamp.ic.students.ra169052;

import java.util.LinkedList;

public class Process {
    public Network network;
    //my clock
    public Clock clock;

    //did i request access
    private boolean accessRequested = false;

    //number of replies i've received for my request
    private int allowCount;

    //list of deferred messages
    private LinkedList<Message> deferredMessages = new LinkedList<>();


    /**
     * Request access to critical region by sending a REQUEST
     * to all peers
     */
    private void requestAccess(){
        accessRequested = true;
        allowCount = 0;
        network.broadcast(new Message(clock, Message.Action.REQUEST));
        clock.increment();
    }

    /**
     * Do work on the critical region
     */
    private void doProcess(){
        sleepFor(1000);
    }

    /**
     * Handle messages previously deferred
     */
    private void sendDeferredMessages() {
        while (!deferredMessages.isEmpty()){
            handleMessage(deferredMessages.removeFirst());
        }
    }

    /**
     * Starts thread for handling incoming messages
     */
    public void startMessengerThread(){
        new Thread(() -> {
            while(true) {
                //try to get a message for this replica
                Message message = network.getMessageFor(clock.pid);
                if(message != null){
                    handleMessage(message);
                }

                sleepFor(500);
            }
        }).start();
    }

    /**
     * Handles incoming message
     * @param message
     */
    private void handleMessage(Message message) {
        switch (message.action){
            case REQUEST:
                Clock lowestClock = Clock.GetLowestClock(clock, message.clock);
                //send allow reply only if (i'm not interested OR my clock is not the lowest)
                boolean allow = !accessRequested || lowestClock != clock;

                if(allow){
                    int destination = message.clock.pid;
                    message.clock = clock;
                    message.action = Message.Action.ALLOW;
                    network.sendTo(message, destination);
                }else{
                    //save this message for later.
                    //it will be handled again when i've used the critical region
                    deferredMessages.add(message);
                }
                break;
            case ALLOW:
                if(accessRequested) {
                    allowCount++;
                    //only enter critical region when every peer
                    //has replied with ALLOW
                    if (allowCount == network.getPeerCount()) {
                        accessRequested = false;
                        //use critical region
                        doProcess();
                        //handle previously deferred messages
                        sendDeferredMessages();
                    }
                }
                break;
        }
    }

    /**
     * Makes thread sleep for around milis ms
     * @param milis
     */
    private void sleepFor(int milis){
        try {
            Thread.sleep(milis + (int) (milis * 0.5 * Math.random()));
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {

    }
}
