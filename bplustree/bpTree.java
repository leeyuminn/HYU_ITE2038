import java.io.*;
import java.util.*;

public class bpTree {

    private static int degree;
    private static int nodecount = 0; // node count
    private static bpNode root = new bpNode();
    public static ArrayList<bpNode> Tree = new ArrayList<>();
    public static ArrayList<Integer> leaf = new ArrayList<>();

    //(1)Creation
    public static void creation(String indexFile, int d) {

        degree = d;

        try {
            FileWriter filewriter = new FileWriter(indexFile); //덮어쓰기(추가x)
            //indexFIle:파일경로
            filewriter.write("degree: " + degree + "\r\n"); //degree를 파일에 출력

            //filewriter.flush(); (<-버퍼사용시) 버퍼에 쌓인 데이터 파일로

            filewriter.close(); //파일 닫음

        }catch(IOException e) {
            e.printStackTrace();
        }


    }

    //(2)Insertion
    public static void insertion(String indexFile, String input) {

        read(indexFile);
        File inputFile = new File("./" + input);
        try {
            FileReader filereader = new FileReader(input);
            BufferedReader bufferedreader =new BufferedReader(filereader);

            String inputReadLine; //계속 다음 줄 넣어야 해 = bufferedreader.readLine();

            while((inputReadLine = bufferedreader.readLine()) != null){
                String[] IndexValuePair = inputReadLine.split(","); //,로 구분해서 저장.
                insert(Integer.parseInt(IndexValuePair[0]),Integer.parseInt(IndexValuePair[1])); //insert(key, value)
            }

            bufferedreader.close();
            filereader.close();

        }catch (IOException e) {
            e.printStackTrace();
        }

        //save(update) index.dat파일에 올리기(+덮어쓰기)
        save(indexFile);


    }

    //(3)Deletion
    public static void deletion(String indexFile, String deletefile) {
        read(indexFile);


    }


    //(4)Search Single Key
    public static void searchSingleKey(String indexFile, int searchkey) {



    }

    //(5)Ranged Search
    public static void rangedSearch(String indexFile, int startKey, int endKey) {



    }

    //read(현재 파일내용 읽어와야 할 듯...)
    public static void read(String indexFile) {

        try {
            File file = new File("./" + indexFile);
            FileReader filereader = new FileReader(file);
            BufferedReader bufferedreader = new BufferedReader(filereader);

            String indexReadLine;

            while((indexReadLine = bufferedreader.readLine()) != null){
                String[] content = indexReadLine.split("\\s"); //띄어쓰기로 구분. "\\s" = " "
                String type = content[0];

                //degree
                if(type.equals("degree:")){
                    degree = Integer.parseInt(content[1]);
                    break;
                }

                // | parent - nownode
                else if(type.equals("|")){
                    int parentNum = Integer.parseInt(content[1]);
                    int mineNum = Integer.parseInt(content[2]);

                    // 그 다음줄에 index value ( "/"로 구분)
                    indexReadLine = bufferedreader.readLine();
                    content = indexReadLine.split(" / "); // content안에 {index value,..}배열
                    //하나씩 받아와야 하니까..

                    bpNode tempNode = new bpNode();

                    for(String indexvalueline : content){
                        //또 잘라 index랑 value랑
                        String[] indexvaluecontent = indexvalueline.split("\\s");
                        int index = Integer.parseInt(indexvaluecontent[0]); //index
                        int value = Integer.parseInt(indexvaluecontent[1]); //value

                        tempNode.m++;
                        tempNode.p.add(new bpNode.index(index, value));
                    }

                    //tree에 하나씩 추가하기
                    for(int i = 0; i <= mineNum - Tree.size(); i++)
                        Tree.add(null);
                    Tree.add(mineNum, tempNode);

                    //parent연결하기
                    if(parentNum == 0)
                        root = tempNode; //temp가 루트
                    else{
                        bpNode parent = Tree.get(parentNum);

                        for(int i = 0; i <parent.m; i++){

                            if(parent.p.get(i).leftchild == null){ //자리 찾아가서
                                parent.p.get(i).leftchild = tempNode; //연결 완!
                                break;
                            }
                            //꽉 찼음(다 돌았는데도) 오른쪽에 연결해야함.
                        }
                        parent.rightChild = tempNode;
                    }
                    //leaf sibling 연결하기

                }

                //leaf
                else if(type.equals("*")){

                    String[] leafList = indexReadLine.split("\\s");

                    for(int i = 0; i < (leafList.length - 1); i++){
                        bpNode node = Tree.get(Integer.parseInt(leafList[i]));
                        node.rightChild = Tree.get(Integer.parseInt(leafList[i + 1]));
                    }
                }
            }

            bufferedreader.close();
            filereader.close();

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void insert(int index, int value){

        bpNode.index newIndex = new bpNode.index(index, value);
        bpNode correctNode = findCorrectNode(index);
        correctNode.p.add(findCorrectIndex(correctNode, index),newIndex);
        correctNode.m++;

        if(correctNode.m >= degree){
            splitNode(correctNode);
        }
    }

    public static bpNode findCorrectNode(int index){

        bpNode checkNode = root;

        //위에서부터 입력받은 인덱스값이 들어갈 자리를 찾아 내려가기.
        while(true){
            if(is_leaf(checkNode)){
                return checkNode;
            }
            else{
                int nodeSize = checkNode.p.size();

                 // 크면 오른쪽sibling으로 이동
                if(checkNode.p.get((nodeSize)-1).index < index){
                    checkNode = checkNode.RightNode;
                    continue;
                }
                else{
                    for(int i = 0; i < checkNode.m; i++){

                        //작으면 index의 왼쪽차일드
                        if (index < checkNode.p.get(i).index){
                            checkNode = checkNode.p.get(i).leftchild;
                            break;
                        }
                    }
                    return checkNode;
                }
            }
        }
    }

    public static int findCorrectIndex(bpNode Node, int key){
        int nodeSize = Node.m;
        int correct = 0;

        if(Node.p.get((nodeSize-1)).index < key)
            correct = nodeSize;
        else {
            for (int i = 0; i < nodeSize; i++) {

                if (key < Node.p.get(i).index) {
                    correct = i;
                    break;
                }
            }
        }
        return correct;
    }

    public static void splitNode(bpNode Node){

        int MidPoint = degree / 2; //parent로 올릴 중간노드
        int MidIndex = Node.p.get(MidPoint).index;
        int MideValue = Node.p.get(MidPoint).value;

        bpNode rightNewNode = new bpNode(); // 두개 쪼개야 하니까 오른쪽 노드를 만들어.
        bpNode leftNewNode = new bpNode();
        //roghtNewNode 채워주기.

        //(1) m
        //degree가 짝수인 경우
        if(degree % 2 == 0)
            rightNewNode.m = MidPoint;
        //degree가 홀수인 경우
        else
            rightNewNode.m = MidPoint + 1;

        //(2) p
        for(int i = MidPoint; i <degree; i++){
            bpNode.index put = Node.p.get(i);
            rightNewNode.p.add(new bpNode.index(put.index, put.value, put.leftchild));
        }

        //(3) rightChild
        rightNewNode.rightChild = Node.rightChild;


        //(1) root가 leaf인데 overflow난 경우
        if(Node == root){
            //m
            leftNewNode.m = MidPoint;
            //p
            for(int i = 0; i < MidPoint; i++){
                bpNode.index put = root.p.get(i);
                leftNewNode.p.add(new bpNode.index(put.index, put.value, put.leftchild));
            }
            //rightCild
            leftNewNode.rightChild = rightNewNode; //leaf 니까 sibling으로

            //root 다시 만들어서 미드포인트값 올려주기.
            root.rightChild = rightNewNode; //먼저 연결해야 안날라감.
            root.m = 1; //하나만 올릴거니까
            root.p.clear(); //싹다 지우고
            root.p.add(new bpNode.index(MidIndex, MideValue, leftNewNode));
        }
        //(2) leaf overflow (root아님)
        //      -> parent노드에 pair 추가
        else{
            Node.rightChild = rightNewNode;
            Node.m = MidPoint;
            Node.p.subList(MidPoint, degree).clear(); // MidPoint~degree까지 부분만 지우기.

            // parent node 찾아야...
            bpNode Parent = Node.Parent;

            //parent m 하나 늘리고
            Parent.m++;

            //midpoint해당값 넣어주기. (넣을 자리 찾기)
            bpNode.index put = Node.p.get(MidPoint);

            if(Parent.p.get((Parent.p.size()-1)).index < put.index){
                Parent.p.add(put);
                Parent.rightChild = rightNewNode;
            }
            else{
                for(int i = 0; i <Parent.p.size(); i++){
                    //작으면 고 앞에
                    if(Parent.p.get(i).index > put.index){
                        Parent.p.add(i, put);
                        Parent.p.get(i + 1).leftchild = rightNewNode;

                        break;
                    }
                }
                //음...
                leftNewNode.Parent = Parent; //Parent.Parent
                rightNewNode.Parent = Parent;

                if(Parent.m >= degree){
                    splitParent(Parent);
                }
            }
            //Parent.p.add(new bpNode.index(put.index, put.value, put.leftchild));
        }
    }

    public static void splitParent(bpNode node){

        int MidPoint = degree / 2; //새 parent로 올릴 중간노드
        int MidIndex = node.p.get(MidPoint).index;
        int MideValue = node.p.get(MidPoint).value;

        bpNode rightNewNode = new bpNode(); // 두개 쪼개야 하니까 오른쪽 노드를 만들어.
        bpNode leftNewNode = new bpNode(); //*(1)경우에만 필요함.

        //(1) m
        // 짝수인 경우
        if(node.m % 2 == 0) //split할 parent의 m이
            rightNewNode.m = MidPoint -1;
        // 홀수인 경우
        else
            rightNewNode.m = MidPoint;

        //(2) p
        for(int i = MidPoint + 1; i < node.m; i++){
            bpNode.index put = node.p.get(i);
            rightNewNode.p.add(new bpNode.index(put.index, put.value, put.leftchild));
        }

        //(3) rightChild
        rightNewNode.rightChild = node.rightChild;


        //*만일 그게 root라면 ->newroot 생성
        if(node == root){
            //m
            leftNewNode.m = MidPoint;
            //p
            for(int i = 0; i < MidPoint; i++){
                bpNode.index put = root.p.get(i);
                leftNewNode.p.add(new bpNode.index(put.index, put.value, put.leftchild));
            }
            //rightCild
            leftNewNode.rightChild = node.p.get(MidPoint).leftchild; //leaf아니니까
                                                            // 쪼개진 오른쪽 노드의 왼쪽이었던 child 가리켜야 함.

            //root 다시 만들어서 미드포인트값 올려주기.
            root.rightChild = rightNewNode; //먼저 연결해야 안날라감.
            root.m = 1; //하나만 올릴거니까
            root.p.clear(); //싹다 지우고
            root.p.add(new bpNode.index(MidIndex, MideValue, leftNewNode));
        }
        //*아니라면 ->위부모에게 midpoint pair add
        else{
            bpNode newParentNode = new bpNode(); //새 부모노드

            newParentNode = node.Parent; //#


            //node 줄여주기
            node.rightChild = node.p.get(MidPoint).leftchild;
            node.m = MidPoint;
            node.p.subList(MidPoint, node.m).clear();

            //새 부모 채우기.
            newParentNode.m++;
            bpNode.index put = new bpNode.index(MidIndex, MideValue, node);
            newParentNode.p.add(newParentNode.m, put); //leftchild론 split하고 있는 현재 노드

            if(newParentNode.m == (degree-1)){ //꽉 찼다면
                newParentNode.rightChild = rightNewNode;
            }
            else{
                newParentNode.p.get(newParentNode.m + 1).leftchild = rightNewNode;
            }

            if(newParentNode.m >= degree){
                splitParent(newParentNode);
            }
        }
    }

    public static void save(String File){

        FileWriter filewriter = null;


        try {
            filewriter = new FileWriter(File);
            filewriter.write(degree + "\n");
            filewriter.close();
            filewriter = new FileWriter(File, true);

           save_in(filewriter, root, 0);

            filewriter.write("*");


            for(Integer i : leaf)
                filewriter.write(" " + i);

            filewriter.flush();
            filewriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void save_in(FileWriter filewriter, bpNode node, int parentNum){
        nodecount++;
        int mineNum = nodecount;

        try {
            if(node.m == 0)
                return;
            //parent - mine 넘버
            filewriter.write("| " + parentNum + " " + mineNum + "\n");
            //index-value / ...
            for(int i = 0; i < node.m; i++){
                filewriter.write(node.p.get(i).index + " " + node.p.get(i).value + " / ");
            }

            filewriter.write("\n");

            for(int i = 0; i < node.m; i++){
                if(node.p.get(i).leftchild == null){
                    return;
                }
                else{
                    save_in(filewriter, node.p.get(i).leftchild, mineNum);
                }
            }

            //만약 leaf면
            if(node.p.get(0).leftchild == null){
                leaf.add(mineNum);
            }
            //리프 아니면
            else{
                if(node.rightChild != null)
                    save_in(filewriter, node.RightNode, mineNum);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }





    }


    public static boolean is_leaf(bpNode node){

        if(node.m == 0 || node.p.get(0).leftchild == null){ //비어있거나 가리키는 child없음 -> leaf
            return true;
        }
        else {
            return false;
        }
    }

    public static bpNode find_leaf(bpNode node, int index) { //index가 들어갈 leaf노드 반환.

        while (!(is_leaf(node))) { //leaf면 탈출.

            if (node.p.get(0).leftchild == null) {
                break;
            } else {
                int nodeSize = node.p.size();

                if (node.p.get((nodeSize) - 1).index < index) { // 더 크면 오른쪽차일드로 이동.
                    node = node.RightNode;
                    continue;
                } else { //왼쪽이나 사이로 이동해야 한다면. ->다 비교

                    for (int i = 0; i < nodeSize; i++) {
                        //제일 왼쪽으로 이동.
                        if (index < node.p.get(i).index) {
                            node = node.p.get(i).leftchild;
                            break; //
                        }
                    }
                }
            }
        }
        return node;
    }


    public static void main(String[] args)
    {

        //bplustree bpTree = new bplustree(); //what...?!

        String command = args[0];
        String indexFile = args[1];
        //String input = args[2]; ->insert,delete에만 해당. case에서 지정하자

        switch(command) {
            case "-c":
                //
                creation(indexFile, Integer.parseInt(args[2]));
                //System.out.println("Creation complete.");

                break;
            case "-i":
                //
                String inputFile = args[2];
                insertion(indexFile, inputFile);
                //System.out.println("Insertion complete.");

                break;
            case "-d":
                //
                String deleteFile = args[2];
                deletion(indexFile, deleteFile);
                //System.out.println("Deletion complete.");

                break;
            case "-s":
                //
                int searchKey = Integer.parseInt(args[2]);
                searchSingleKey(indexFile, searchKey);
                //System.out.println(searchKey + "found.");

                break;
            case "-r":
                //
                int range1 = Integer.parseInt(args[2]);
                int range2 = Integer.parseInt(args[3]); //range1에서 range2까지 구간검색.
                //range1:start_key, range2:end_key
                rangedSearch(indexFile, range1, range2);
                //System.out.println("Ranged Search [" + range1 + ", " + range2 + "] complete.");

                break;

            default:
                System.out.println("error! Check the Command.\n");

        }
    }
}
