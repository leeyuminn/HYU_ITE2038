import java.io.*;
import java.util.*;

public class bpTree {

    private static int degree;
    private static int nodecount = 0; // node count
    private static bpNode root = new bpNode();
    public static List<bpNode> Tree = new ArrayList<>();
    public static List<Integer> leaf = new ArrayList<>();

    //(1)Creation
    public static void creation(String indexFile, int d) {

        degree = d;

        try {
            FileWriter filewriter = new FileWriter("./" + indexFile);
            //indexFIle:파일경로
            filewriter.write("degree: " + degree + "\r\n"); //degree를 파일에 출력

            //filewriter.flush(); (<-버퍼사용시) 버퍼에 쌓인 데이터 파일로

            filewriter.close(); //파일 닫음

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //(2)Insertion
    public static void insertion(String indexFile, String input) {

        read(indexFile);
        File inputFile = new File("./" + input);
        try {
            FileReader filereader = new FileReader(inputFile);
            BufferedReader bufferedreader = new BufferedReader(filereader);

            String inputReadLine; //계속 다음 줄 넣어야 해 = bufferedreader.readLine();

            while ((inputReadLine = bufferedreader.readLine()) != null) {    //BufferedReader의 readLine() 메소드는 텍스트 파일을 한 줄씩 읽어서 리턴.더 이상 읽을 내용이 없으면, null을 리턴
                String[] IndexValuePair = inputReadLine.split(","); //,로 구분해서 저장.
                insert(Integer.parseInt(IndexValuePair[0]), Integer.parseInt(IndexValuePair[1])); //insert(key, value)
            }

            bufferedreader.close();
            filereader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //save(update) index.dat파일에 올리기
        save(indexFile);
    }

    //(3)Deletion
    public static void deletion(String indexFile, String deletefile) {
        read(indexFile);
    }

    //(4)Search Single Key
    public static void searchSingleKey(String indexFile, int searchkey) {
        read(indexFile);
        bpNode checkNode = root; //위에서부터 체크할 것

        while(!is_leaf(checkNode)){
            //leaf가 아니면 leaf까지 내려가기.

            //중간노드값 모두 출력
            for(int i = 0; i < checkNode.m; i++){
                System.out.println(checkNode.p.get(i).index + ", ");
            }

            //크거나 같으면 rightChild
            if(searchkey >= checkNode.p.get((checkNode.p.size()) - 1).index){
                checkNode = checkNode.rightChild;
            }
            else{
                for(int i = 0; i < checkNode.p.size(); i++){
                    //작으면 leftChild
                    if(searchkey < checkNode.p.get(i).index){
                        checkNode = checkNode.p.get(i).leftchild;
                        break;
                    }
                }
            }
        }

        //root가 leaf고 바로 찾으면 해당값만 출력
        for(int i = 0; i < checkNode.m; i++){
            if(checkNode.p.get(i).index == searchkey){
                System.out.println(checkNode.p.get(i).value);
                return;
            }
        }
        System.out.println("NOT FOUND\n");
    }

    //(5)Ranged Search
    public static void rangedSearch(String indexFile, int startKey, int endKey) {

        read(indexFile);

        //startKey가 있는(없을지도모르잖..?) 리프노드를 찾고 거기부터 순서대로 출력하다가 endKey보다 커지는 순간 out
        bpNode checkNode = root;

        int index1;

        while(true){
            index1 = checkNode.m;

            for(int i = 0; i < checkNode.m; i++){
                //start보다 크거나같아지는 순간찾기
                if(startKey <= checkNode.p.get(i).index){
                    index1 = i;
                    break;
                }
            }

            if(is_leaf(checkNode)){
                break;
            }

            if(index1 == checkNode.m){
                checkNode = checkNode.rightChild;
            }
            else {
                checkNode = checkNode.p.get(index1).leftchild;
            }
        }

        while(true){
            for(int i = 0; i < checkNode.m; i++){

                if(endKey < checkNode.p.get(i).index){
                    return;
                }
                if(startKey <= checkNode.p.get(i).index){
                    System.out.println(checkNode.p.get(i).index + ", " + checkNode.p.get(i).value);
                }
                /*
                while(checkNode.p.get(i).index <= endKey){
                    if(startKey <= checkNode.p.get(i).index){
                        System.out.println(checkNode.p.get(i).index + ", " + checkNode.p.get(i).value);
                    }
                }*/
            }
            if(checkNode.rightChild != null){
                checkNode = checkNode.rightChild;
            }
            else{
                return;
            }
        }
    }

    //read
    public static void read(String indexFile) {

        try {
            File file = new File("./" + indexFile);
            FileReader filereader = new FileReader(file);
            BufferedReader bufferedreader = new BufferedReader(filereader);

            String indexReadLine;

            while ((indexReadLine = bufferedreader.readLine()) != null) {
                String[] content = indexReadLine.split("\\s"); //띄어쓰기로 구분. "\\s" = " "
                String type = content[0];

                //degree
                if (type.equals("degree:")) {
                    degree = Integer.parseInt(content[1]);
                }

                // | parent - nownode
                else if (type.equals("|")) {
                    int parentNum = Integer.parseInt(content[1]);
                    int mineNum = Integer.parseInt(content[2]);

                    // 그 다음줄에 index value ( "/"로 구분)
                    indexReadLine = bufferedreader.readLine(); //그 다음줄
                    String Ivvl[] = indexReadLine.split(" / "); // IvVl안에 {index value,..}배열
                    //하나씩 받아와야 하니까..

                    bpNode tempNode = new bpNode();

                    for (String indexvalueline : Ivvl) { //하나씩 대입
                        //또 잘라 index랑 value랑
                        String[] indexvaluecontent = indexvalueline.split("\\s");
                        int index = Integer.parseInt(indexvaluecontent[0]); //index
                        int value = Integer.parseInt(indexvaluecontent[1]); //value

                        tempNode.m++;
                        tempNode.p.add(new bpNode.index(index, value)); //어차피 인덱스파일에 순서대로 돼있었을테니깐 그냥 넣기
                    }

                    //tree에 하나씩 추가하기
                    for (int i = 0; i <= mineNum - Tree.size(); i++)
                        Tree.add(null);
                    Tree.add(mineNum, tempNode);

                    //tempNode (2)rightchild하고 (1)Parent연결해야함.

                    //(1)parent연결하기 <- ParentNum, MineNum 이용
                    if (parentNum == 0)
                        root = tempNode; //temp가 루트
                    else { //0아니면 해당 번호 노드를 부모로 연결.
                        tempNode.Parent = Tree.get(parentNum); //parent에 담아두고 그 parent의

                        for (int i = 0; i < tempNode.Parent.m; i++) {
                            if (tempNode.Parent.p.get(i).leftchild == null) { //자리 찾아가서 (처음엔 왼쪽부터 순서대로)
                                tempNode.Parent.p.get(i).leftchild = tempNode; //연결 완!
                                break;
                            }
                            //(2)꽉 찼음(다 돌았는데도) 오른쪽에 연결해야함.
                            else {
                                tempNode.Parent.rightChild = tempNode;
                                //break; //굳이긴 할듯
                            }
                        }
                    }
                }

                if(type.equals("*")){
                    String[] leafList = indexReadLine.split("\\s"); //ex. {*, 2, 3, 4}

                    //leaf끼리 연결 연결
                    for (int i = 1; i < (leafList.length - 1); i++) { //# *생각해서 i에 1부터 했는데 맞는지 확인필요
                        bpNode node = Tree.get(Integer.parseInt(leafList[i]));
                        node.rightChild = Tree.get(Integer.parseInt(leafList[i + 1]));
                    }
                }
            }
            bufferedreader.close();
            filereader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void insert(int index, int value) {

        bpNode.index newIndex = new bpNode.index(index, value); //pair 만들어서
        bpNode correctNode = findCorrectNode(index); //노드 찾아서
        correctNode.p.add(findCorrectIndex(correctNode, index), newIndex); //+ 노드 안 자리 찾고, 삽입
        correctNode.m++;

        //일단 삽입했는데 최대 넘어가면 그leaf split해라
        if (correctNode.m >= degree) {
            splitNode(correctNode);
        }
    }

    public static bpNode findCorrectNode(int index) {

        //root부터
        bpNode checkNode = root;

        boolean is_down = false;

        //위에서부터 입력받은 인덱스값이 들어갈 자리를 찾아 내려가기.
        while (true) {
            //내려오다가 걔가 리프면 찾았다! 되는 겨 근데 처음부터 리프일 수 있으니 위에다가
            if (is_leaf(checkNode)) {
                return checkNode;
            } else {
                int nodeSize = checkNode.p.size();

                for (int i = 0; i < checkNode.m; i++) {
                    //작으면 index의 왼쪽차일드
                    if (index < checkNode.p.get(i).index) {
                        checkNode = checkNode.p.get(i).leftchild;
                        is_down = true;
                        break;
                    }
                }
                //젤 오른쪽것보다 크면 오른쪽sibling으로 이동
                if (is_down == false) {
                    checkNode = checkNode.rightChild;
                }
            }
        }
    }

    public static int findCorrectIndex(bpNode Node, int key) {
        int nodeSize = Node.m;
        int correctindex = 0;
        boolean is_find = false;

        /*if(Node.p.get((nodeSize-1)).index < key)
            correctindex = nodeSize;*/
        for (int i = 0; i < nodeSize; i++) {

            if (key < Node.p.get(i).index) {
                correctindex = i;
                is_find = true;
                break;
            }
        }
        //젤크면 오른쪽에 넣어야지.
        if (is_find == false) {
            correctindex = nodeSize;
        }
        return correctindex;
    }

    public static void splitNode(bpNode Node) {

        int MidPoint = degree / 2; //parent로 올릴 중간노드번호
        int MidIndex = Node.p.get(MidPoint).index;
        int MideValue = Node.p.get(MidPoint).value;

        bpNode rightNewNode = new bpNode(); // 두개 쪼개야 하니까 오른쪽 노드를 만들어.

        //rightNewNode 채워주기.
        //(1) m
        //degree가 짝수인 경우
        if (degree % 2 == 0)
            rightNewNode.m = MidPoint;  // o o / o o
            //degree가 홀수인 경우
        else
            rightNewNode.m = MidPoint + 1; // o o / o o o

        //(2) p
        for (int i = MidPoint; i < degree; i++) {
            bpNode.index put = Node.p.get(i);
            rightNewNode.p.add(new bpNode.index(put.index, put.value, put.leftchild)); //leaf였을테니 leftchild는 null일 것
            //처음부터 채우면 됌
        }

        //(3) rightChild
        rightNewNode.rightChild = Node.rightChild;


        //(4)parent 올려주기 (새 루트 만들 건지 이미 있는 부모에 추가할 건지 조건 나누기)

        //-(1) root가 leaf인데 overflow난 경우
        if (Node == root) {

            bpNode leftNewNode = new bpNode();

            //(1) m
            leftNewNode.m = MidPoint;
            //(2) p
            for (int i = 0; i < MidPoint; i++) {
                bpNode.index put = root.p.get(i); // 0 1
                leftNewNode.p.add(new bpNode.index(put.index, put.value, put.leftchild));
            }
            //(3) rightCild
            leftNewNode.rightChild = rightNewNode; //leaf 니까 sibling으로

            //root 초기화, 미드포인트값 올려주기.
            root.rightChild = rightNewNode; //먼저 연결해야 안날라감.
            root.m = 1; //하나만 올릴거니까
            root.p.clear(); //싹다 지우고
            root.p.add(new bpNode.index(MidIndex, MideValue, leftNewNode)); //하나만

            //(4) Parent
            leftNewNode.Parent = root;
            rightNewNode.Parent = root;
        }

        //-(2) leaf overflow (root아님)
        //      -> parent노드에 pair 추가
        else {
            Node.rightChild = rightNewNode;
            Node.m = MidPoint;
            Node.p.subList(MidPoint, degree).clear(); // MidPoint~degree까지 부분만 지우기.

            putinParent(MidIndex, MideValue, Node, rightNewNode);
        }
    }


    //-(2)경우에서 위로 값하나 올려줬더니, overflow된 경우에 또 split해야 함. ->new root생성
    public static void splitParent(bpNode node) {

        //ex. 4 -> 2 / 1(new root) / 1로
        //ex. 5 -> 2 / 1(new root) / 2로
        int MidPoint = degree / 2; //새 parent로 올릴 중간노드
        int MidIndex = node.p.get(MidPoint).index;
        int MideValue = node.p.get(MidPoint).value;

        bpNode rightNewNode = new bpNode(); // 두개 쪼개야 하니까 오른쪽 노드를 만들어.

        //rightNewNode채우기.

        //(1) m
        // 짝수인 경우
        if (node.m % 2 == 0) //split할 parent의 m이  % : 나머지반환
            rightNewNode.m = MidPoint - 1;
            // 홀수인 경우
        else
            rightNewNode.m = MidPoint;

        //(2) p
        for (int i = MidPoint + 1; i < node.m; i++) {
            bpNode.index put = node.p.get(i);
            rightNewNode.p.add(new bpNode.index(put.index, put.value, put.leftchild));
        }

        //(3) rightChild
        rightNewNode.rightChild = node.rightChild;


        //*만일 그게 root라면 ->newroot 생성
        if (node == root) {
            bpNode leftNewNode = new bpNode(); //요경우에만 필요함.
            //채우기
            //(1) m
            leftNewNode.m = MidPoint;

            //(2) p
            for (int i = 0; i < MidPoint; i++) { // 0 1
                bpNode.index put = root.p.get(i);
                leftNewNode.p.add(new bpNode.index(put.index, put.value, put.leftchild));
            }

            //(3) rightCild
            leftNewNode.rightChild = node.p.get(MidPoint).leftchild; //leaf아니니까
            // 쪼개진 오른쪽 노드의 왼쪽이었던 child 가리켜야 함.
            //root 다시 만들어서 미드포인트값 올려주기.
            root.rightChild = rightNewNode; //먼저 연결해야 안날라감.
            root.m = 1; //하나만 올릴거니까
            root.p.clear(); //싹다 지우고
            root.p.add(new bpNode.index(MidIndex, MideValue, leftNewNode));
        }

        //*아니라면 ->위부모에게 midpoint pair add
        else {

            //node 줄여주기
            node.rightChild = node.p.get(MidPoint).leftchild;
            node.m = MidPoint;
            node.p.subList(MidPoint, node.m).clear();

            //새 부모 채우기.
            putinParent(MidIndex, MideValue, node, rightNewNode);
        }
    }

    public static void putinParent(int putindex, int putvalue, bpNode connectleft, bpNode connectright) {

        //parent 찾아서 연결.
        bpNode parent = connectleft.Parent;

        //(1) m
        parent.m++;

        bpNode.index newIndex = new bpNode.index(putindex, putvalue); //pair만들고

        //findCorrectIndex로 하면 안되는 이유: leaf까지 내려가지 않기 때문.

        boolean is_put = false;

        //(2) p
        for (int i = 0; i < parent.p.size(); i++) {
            if (putindex < parent.p.get(i).index) {
                parent.p.add(i, newIndex);
                is_put = true;
                //(3) Child 연결 #
                //집어넣은 pair의 왼쪽차일드를 스필릿된노드
                parent.p.get(i).leftchild = connectleft;
                //집어넣은 pair의 다음 pair의 왼쪽 차일드를 뉴오른쪽노드로
                parent.p.get(i + 1).leftchild = connectright;
                //부모노드의 라이트를 원래 부모의 라이트 그대로,
            }

            if (is_put == false) {
                parent.p.add(newIndex); //그냥 맨뒤에다가

                //(3) Child 연결
                parent.p.get(i).leftchild = connectleft;
                parent.rightChild = connectright;
            }
        }
        connectleft.Parent = parent;
        connectright.Parent = parent;

        if (parent.m >= degree) {
            //split
            splitParent(parent);
        }
    }

    public static void save(String File) {

        try {
            File indexFile = new File("./" + File);
            FileWriter filewriter = new FileWriter(indexFile); //덮어쓰기.
            filewriter.write("degree: " + degree + "\n");
            filewriter.close();

            filewriter = new FileWriter(File, true); //-> 기존파일내용 끝에 데이터 추가

            //노드들( | ~~~ )
            save_in(filewriter, root, 0);

            //리프들( * ~~~ )
            filewriter.write("*");

            for (Integer i : leaf)
                filewriter.write(" " + i);

            filewriter.flush();
            filewriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save_in(FileWriter filewriter, bpNode node, int parentNum) {
        nodecount++;
        int mineNum = nodecount;

        try {
            if (node.m == 0)
                return;
            //parent - mine 넘버
            filewriter.write("| " + parentNum + " " + mineNum + "\n");
            //index-value / ...
            for (int i = 0; i < node.m; i++) {
                filewriter.write(node.p.get(i).index + " " + node.p.get(i).value + " / ");
            }
            filewriter.write("\n");

            for (int j = 0; j < node.m; j++) {

                if(node.p.get(j).leftchild != null)
                    save_in(filewriter, node.p.get(j).leftchild, mineNum);
            }

            //리프 아니면
            if  (node.rightChild != null && node.p.get(0).leftchild != null){
                save_in(filewriter, node.rightChild, mineNum); //#입력값확인필요
            }
            //만약 leaf면
            if(node.p.get(0).leftchild == null){ //child가 없으면
                leaf.add(mineNum);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean is_leaf(bpNode node) {

        if (node.m == 0 || node.p.get(0).leftchild == null) { //비어있거나 가리키는 child없음 -> leaf
            return true;
        } else {
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
                    node = node.rightChild;
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


    public static void main(String[] args) {
        String command = args[0];
        String indexFile = args[1];
        //String input = args[2]; ->insert,delete에만 해당. case에서 지정하자

        switch (command) {
            case "-c":
                creation(indexFile, Integer.parseInt(args[2]));
                //System.out.println("Creation complete.");
                break;
            case "-i":
                String inputFile = args[2];
                insertion(indexFile, inputFile);
                //System.out.println("Insertion complete.");
                break;
            case "-d":
                String deleteFile = args[2];
                deletion(indexFile, deleteFile);
                //System.out.println("Deletion complete.");
                break;
            case "-s":
                int searchKey = Integer.parseInt(args[2]);
                searchSingleKey(indexFile, searchKey);
                //System.out.println(searchKey + "found.");
                break;
            case "-r":
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
