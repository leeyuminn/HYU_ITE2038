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


    int m; // indexpair 수
    ArrayList<index> p; //degree-1 만큼
    bpNode rightChild;

    //leaf라면 sibling끼리 연결하기 위함.
    bpNode RightNode;
    bpNode LeftNode;

    bpNode Parent; //부모는 하나

    bpNode(){
        this.m = 0;
        this.p = new ArrayList<>();
        this.rightChild = null; //pair full인 경우 오른쪽 child
                                //pair full 아닌 경우 오른쪽 silbling
        this.LeftNode = null;
        this.Parent = null;
    }

}
