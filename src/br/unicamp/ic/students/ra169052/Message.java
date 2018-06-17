package br.unicamp.ic.students.ra169052;

public class Message {
    public enum Action {
        REQUEST, ALLOW
    }

    public Clock clock;
    public Action action;

    public Message(Clock clock, Action action){
        this.clock = clock;
        this.action = action;
    }
}
