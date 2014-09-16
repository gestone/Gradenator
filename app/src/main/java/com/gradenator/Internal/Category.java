package com.gradenator.Internal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Justin on 8/25/2014.
 */
public class Category {

    private String title;
    private double weight;
    private List<Assignment> allAssignments;

    public Category(String title, double weight) {
        this.title = title;
        this.weight = weight;
        allAssignments = new ArrayList<Assignment>();
    }

    public void addAssignment(String title, double earned, double total) {
        allAssignments.add(new Assignment(title, earned, total));
    }

    public Assignment removeAssignment(String title) {
        for (int i = 0; i < allAssignments.size(); i++) {
            if (allAssignments.get(i).getTitle().equals(title)) {
                return allAssignments.remove(i);
            }
        }
        return null;
    }

    public List<Assignment> getAllAssignments() {
        return allAssignments;
    }

    public double getWeight() {
        return weight;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

}
