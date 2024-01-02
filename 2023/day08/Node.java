import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(getName(), node.getName()) && Objects.equals(l.getName(), node.l.getName()) && Objects.equals(r.getName(), node.r.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), l.getName(), r.getName());
    }

    @Override
    public String toString() {
        return "Node[" +
                "name=" + name +
                /*
                ", left=" + l.getName() +
                ", right=" + r.getName() +
                 */
                ']';
    }
}
