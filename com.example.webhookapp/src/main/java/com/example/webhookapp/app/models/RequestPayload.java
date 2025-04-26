package com.example.webhookapp.app.model;

import java.util.List;

public class RequestPayload {

    private Integer n; // Only for Question 2 (Nth Level Followers)
    private Integer findId; // Only for Question 2
    private List<Users> users; // Common for both questions

    public RequestPayload() {
    }

    public RequestPayload(Integer n, Integer findId, List<Users> users) {
        this.n = n;
        this.findId = findId;
        this.users = users;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public Integer getFindId() {
        return findId;
    }

    public void setFindId(Integer findId) {
        this.findId = findId;
    }

    public List<Users> getUsers() {
        return users;
    }

    public void setUsers(List<Users> users) {
        this.users = users;
    }
}
