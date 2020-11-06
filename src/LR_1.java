import java.util.*;
//completed
/**
 * get JOSN from LL(1) proceessing   for  webServelt
 * @author mhl(Mhlsky)
 */
public class LR_1 {
    static String inStr = "i*i+i#";
    static String[] grammarStr = {"E->E+T", "E->T", "T->T*F", "T->F",
            "F->(E)", "F->i"};//相关文法,(文法中若有空，用'#'输入)
    static Character start = 'E';
    static Character itemStart = 'S';//扩展文法起始
    static HashMap<Character, ArrayList<String>> production = new HashMap<>();//产生式
    static HashSet<Character> VtSet = new HashSet<>();//终结符集
    static HashSet<Character> VnSet = new HashSet<>();//非终结符
    static HashSet<Character> TOtalSet = new HashSet<>();//全体符号
    static HashMap<Integer, HashMap<Character, String>> ACTION = new HashMap<>();
    static HashMap<Integer, HashMap<Character, Integer>> GOTO = new HashMap<>();
    //内部类：
    //栈内元素,二元组（状态，文法符号）
    static class Item_stack {
        public Item_stack(int state, Character sign) {
            this.state = state;
            this.sign = sign;
        }

        int state;
        Character sign;
    }
    //项目集规范族元素，二元组(项目，展望符）
    static class Item_collection {
        String str;
        Character ch;

        public Item_collection(String str, Character ch) {
            this.str = str;
            this.ch = ch;
        }

        //重写hashcode 、equal  利用set 去重
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Item_collection that = (Item_collection) o;
            return Objects.equals(str, that.str) &&
                    Objects.equals(ch, that.ch);
        }

        @Override
        public int hashCode() {
            return Objects.hash(str, ch);
        }
    }
    static ArrayList<HashSet<Item_collection>> collection = new ArrayList<>();//项目集规范族
    static Stack<Item_stack> stack = new Stack<>();//符号栈
    //输出状态存储
    static ArrayList<Vector<String> > printlist= new ArrayList<>();


    public static void main(String[] args) {
        //初始化
        init();
        getCollection();
        initTable();
        creatTable();
        System.out.printf("%10s%10s%10s\n","状态","符号","输入串");
        startLR_1();
        System.out.println("---------------");

    }
    /**
     * 生成产生式Map(production)，划分终结符（vt）与非终结符（vn），拓展文法S->···,
     */
    static void init() {
        //生成产生式Map(production)
        for (String str : grammarStr
        ) {
            //将“|”相连的产生式分开
            String[] strings = str.split("->")[1].split("\\|");
            //非终结符
            char Vch = str.charAt(0);
            ArrayList<String> list = production.containsKey(Vch) ? production.get(Vch) : new ArrayList<String>();
            for (String S : strings
            ) {
                list.add(S);
            }
            production.put(str.charAt(0), list);
            VnSet.add(Vch);
        }
        //寻找终结符
        for (Character ch : VnSet
        ) {
            for (String str : production.get(ch)
            ) {
                for (Character c : str.toCharArray()
                ) {
                    if (!VnSet.contains(c))
                        VtSet.add(c);
                }
            }

        }
        //全部符号
        TOtalSet.addAll(VnSet);
        TOtalSet.addAll(VtSet);
        //拓展文法
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(start + "");
        production.put(itemStart, arrayList);


    }
    /**
     * 初始化ACTION/GOTO
     */
    static void initTable() {
        //初始化表格
        HashSet<Character> vt = new HashSet<>();
        vt.addAll(VtSet);
        vt.add('#');
        for (int i = 0; i < collection.size(); i++) {
            HashMap<Character, String> map = new HashMap<>();
            for (Character ch : vt
            ) {
                map.put(ch, "ERROR");
            }
            ACTION.put(i, map);
        }
        for (int i = 0; i < collection.size(); i++) {
            HashMap<Character, Integer> map = new HashMap<>();
            for (Character ch : VnSet
            ) {
                map.put(ch, -1);
            }
            GOTO.put(i, map);
        }
    }
    /**
     * 求任意串的first集
     *
     * @param s
     * @return Set
     */
    static HashSet<Character> getFirst(String s) {
        HashSet<Character> set = new HashSet<>();
        int lenth = 0;
        // 从左往右扫描该式
        int i = 0;
        while (i < s.length()) {
            char tn = s.charAt(i);
            HashSet<Character> tvSet = getFirst(tn);
            // 将其非空 first集加入左部
            for (Character tmp : tvSet)
                if (tmp != '#')
                    set.add(tmp);
            // 若包含空串 处理下一个符号
            if (tvSet.contains('#'))
                i++;
                // 否则结束
            else
                break;
            // 到了尾部 即所有符号的first集都包含空串 把空串加入
            if (i == s.length()) {
                set.add('#');
            }
        }
        return set;
    }
    static HashSet<Character> getFirst(Character ch) {
        HashSet<Character> set = new HashSet<>();
        if (VtSet.contains(ch) || ch == '#') {
            set.add(ch);
            return set;
        }
        //ch为vn
        for (String str : production.get(ch)
        ) {
            int i = 0;
            while (i < str.length()) {
                char curChar = str.charAt(i);
                if (curChar == ch)
                    break;
                HashSet<Character> set1 = getFirst(curChar);
                for (Character tmp : set1
                ) {
                    if (tmp != '#')
                        set.add(tmp);
                }
                if (set1.contains('#'))
                    i++;
                else
                    break;
            }
            if (i == str.length())
                set.add('#');
        }
        return set;
    }
    /**
     * 求项目集的闭包
     *
     * @param I 项目集I的下表
     */
    static HashSet<Item_collection> CLOSURE(HashSet<Item_collection> I) {
        HashSet<Item_collection> set = new HashSet<>();
        set.addAll(I);
        int lastNum = set.size();
        int curNum = 9999;
        while (curNum != lastNum) {
            lastNum = set.size();
            HashSet<Item_collection> Add = new HashSet<>();
            for (Item_collection item : set
            ) {
                String string = item.str;
                int pos = string.indexOf("·");
                //如果'·'不是在最后
                if (pos + 1 != string.length()) {

                    Character ch = string.charAt(pos + 1);//'·'后的符号
                    if (VnSet.contains(ch)) {
                        String last = string.substring(pos + 2);
                        HashSet<Character> set1 = getFirst(last + item.ch);
                        for (Character vt : set1
                        ) {

                            for (String s : production.get(ch)
                            ) {

                                String ss = ch + "->" + "·" + s;
                                Item_collection a = new Item_collection(ss, vt);
                                Add.add(a);
                            }

                        }
                    }
                }

            }
            set.addAll(Add);
            curNum = set.size();
        }
        return set;
    }
    /**
     * 状态转换函数
     *
     * @param I
     * @param X
     */
    static HashSet<Item_collection> GO(int I, Character X) {
        HashSet<Item_collection> setI = collection.get(I);
        HashSet<Item_collection> setJ = new HashSet<>();
        for (Item_collection item : setI
        ) {
            String s = item.str;
            int pos = s.indexOf("·");
            if (pos + 1 == s.length() || s.charAt(pos + 1) != X)
                continue;
            String regex = "\\·" + X;
            if (X == '(' || X == ')' || X == '+' || X == '*')
                regex = "\\·" + "\\" + X;
            Item_collection a = new Item_collection(s.replaceAll(regex, X + "·"), item.ch);
            setJ.add(a);
        }

        return CLOSURE(setJ);
    }
    /**
     * 求项目集规范族
     */
    static void getCollection() {
        //初始项目集I
        HashSet<Item_collection> I = new HashSet<>();
        Item_collection item_collection = new Item_collection(itemStart + "->" + "·" + start, '#');
        I.add(item_collection);

        HashSet<Item_collection> set = CLOSURE(I);
        collection.add(set);

        int lastNum = collection.size();
        int curNum = 9999;
        while (lastNum != curNum) {
            lastNum = collection.size();
            for (Character ch : TOtalSet
            ) {
                for (int i = 0; i < lastNum; i++) {
                    HashSet<Item_collection> goSet = GO(i, ch);
                    if(goSet.size()==0)
                        continue;
                    Boolean flag = true;
                    for (HashSet<Item_collection> collections : collection
                    ) {
                        if (collections.equals(goSet))
                            flag = false;
                    }
                    if (flag)
                        collection.add(goSet);
                }
            }
            curNum = collection.size();
        }
}
    /**
     * 构造分析表
     */
    static void creatTable() {
        HashSet<Character> vt = new HashSet<>();
        vt.addAll(VtSet);
        vt.add('#');
        for (int i = 0; i < collection.size(); i++) {

            HashSet<Item_collection> set = collection.get(i);

            for (Item_collection item : set
            ) {
                int pos = item.str.indexOf("·");
                if (pos + 1 == item.str.length()) {
                    if (item.str.charAt(0) == itemStart)
                        ACTION.get(i).put('#', "acc");
                    else {
                        int num_production = -1;//求非终结符的第几个产生式
                        ArrayList<String> arrayList = production.get(item.str.charAt(0));
                        for (int j = 0; j < arrayList.size(); j++) {
                            if (item.str.contains(arrayList.get(j))) {
                                num_production = j;
                                break;
                            }
                        }

                        ACTION.get(i).put(item.ch, "规约r：" + item.str.charAt(0) + num_production);


                    }

                }
                else {
                    Character ch = item.str.charAt(pos + 1);
                    if (VtSet.contains(ch)) {
                        int Ij = findGo(GO(i, ch));
                        if (Ij != -1)
                            ACTION.get(i).put(ch, "移进s" + Ij);
                    } else {
                        int Ij = findGo(GO(i, ch));
                        if (Ij != -1)
                            GOTO.get(i).put(ch, Ij);
                    }
                }
            }

        }
    }
    /**
     *查找GO函数生成的项目集IK的序号:k
     */
    static int findGo(HashSet<Item_collection> I) {
        for (int i = 0; i < collection.size(); i++) {
            if (collection.get(i).equals(I))
                return i;
        }

        return -1;
    }
    /**
     * 执行LR(1)分析器
     */
    static void startLR_1() {
        Item_stack intItem = new Item_stack(0, '#');
        stack.push(intItem);
        int insPionter = 0;
        Character inch = inStr.charAt(insPionter);
        String state = ACTION.get(0).get(inch);
        print(insPionter);
        while (state != "acc" && state != "ERROR") {
            if (state.contains("移进")) {
                int nextState = new Integer(state.charAt(state.length() - 1)-48);
                if(state.charAt(state.length() - 2)<=57&&state.charAt(state.length() - 1)>=48)
                    nextState =  Integer.valueOf(state.substring(state.length()-2));

                Item_stack item = new Item_stack(nextState, inStr.charAt(insPionter));
                stack.push(item);
                insPionter++;
                print(insPionter);

            } else if (state.contains("规约")) {

                Integer num_production = new Integer(state.charAt(state.length() - 1)-48);
                Character A = state.charAt(state.length() - 2);
                String p = production.get(A).get(num_production);
                for (int i = p.length() - 1; i >= 0; i--) {
                    Item_stack top = stack.peek();
                    if (top.sign == p.charAt(i))
                        stack.pop();
                }
                Item_stack top = stack.peek();
                int nextState = GOTO.get(top.state).get(A);
                Item_stack item = new Item_stack(nextState, A);
                stack.push(item);
                print(insPionter);
            }
            state = ACTION.get(stack.peek().state).get(inStr.charAt(insPionter));
        }
        if (state.contains("acc")) {
            System.out.println("分析成功");
        } else {
            System.out.println("ERROR");
        }

    }
    static void print(int p){
        String a ="";
        String b = "";
        String c = "";
        for (Item_stack s: stack
             ) {
            a+=s.state+"、";
        }
        for (Item_stack s: stack
        ) {
            b+=s.sign;
        }
        c=inStr.substring(p);
        System.out.printf("%-20s%-10s%-10s\n",a,b,c);
        Vector<String> print= new Vector<>();
        print.add(a);
        print.add(b);
        print.add(c);
        printlist.add(print);
    }

    static void start(){
        //初始化
        init();
        getCollection();
        initTable();
        creatTable();
        System.out.printf("%10s%10s%10s\n","状态","符号","输入串");
        startLR_1();
        System.out.println("---------------");
    }
}
