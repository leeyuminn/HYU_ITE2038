import java.util.*;

public class bpNode {
    static class index {

        int index;
        int value;
        bpNode leftchild;

        public index(int index, int value) {
            this.index = index;
            this.value = value;
            this.leftchild = null;

        }

        public index(int index, int value, bpNode node) {
            this.index = index;
            this.value = value;
            this.leftchild = node;

        }

        public int getIndex() {
            return this.index;
        }

        public int getValue() {
            return this.value;
        }

        public bpNode getLeftcihld(){
            return this.leftchild;
        }
    }


    int m; // indexpair 수 = degree-1 만큼
    List<index> p;
    bpNode rightChild;

    bpNode Parent; //부모는 하나

    bpNode(){
        this.m = 0;
        this.p = new ArrayList<>();
        this.rightChild = null; //pair full인 경우 오른쪽 child
        //pair full 아닌 경우 오른쪽 silbling
        this.Parent = null;
    }
}
