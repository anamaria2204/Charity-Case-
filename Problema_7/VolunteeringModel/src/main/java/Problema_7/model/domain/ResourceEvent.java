package Problema_7.model.domain;

public class ResourceEvent {
    private String action;
    private Object data;

    public ResourceEvent(){

    }

    public ResourceEvent(String action, Object data) {
        this.action = action;
        this.data = data;
    }

    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }
}
