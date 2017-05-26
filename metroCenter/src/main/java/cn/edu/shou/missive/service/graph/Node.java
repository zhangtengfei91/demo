package cn.edu.shou.missive.service.graph;

import java.util.ArrayList;
import java.util.List;

public class Node extends GraphElement {
    private String type;
    private boolean active;
    private List<Edge> edges = new ArrayList<Edge>();
    /**
     * 进入这个节点的所有连线.
     */
    private List<Edge> incomingEdges = new ArrayList<Edge>();

    /**
     * 外出这个节点的所有连线.
     */
    private List<Edge> outgoingEdges = new ArrayList<Edge>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public List<Edge> getIncomingEdges() {
        return incomingEdges;
    }

    public void setIncomingEdges(List<Edge> incomingEdges) {
        this.incomingEdges = incomingEdges;
    }

    public List<Edge> getOutgoingEdges() {
        return outgoingEdges;
    }

    public void setOutgoingEdges(List<Edge> outgoingEdges) {
        this.outgoingEdges = outgoingEdges;
    }
}
