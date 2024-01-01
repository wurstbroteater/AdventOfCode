public class Node {

    private String name;
    private Node l;
    private Node r;

    public Node(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setL(Node l) {
        this.l = l;
    }

    public void setR(Node r) {
        this.r = r;
    }

    public Node getSibling(String dir) {
        if (dir.toLowerCase().charAt(0) == 'l') {
            return l;
        } else {
            return r;
        }
    }

    @Override
    public String toString() {
        return "Node[" +
                "name=" + name +
                ", left=" + l.getName() +
                ", right=" + r.getName() +
                ']';
    }
}
