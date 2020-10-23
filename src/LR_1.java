import java.util.*;

/**
 * get JOSN from LL(1) proceessing   for  webServelt
 *
 * @author mhl(Mhlsky)
 */
public class LR_1 {
    static String inStr = "i+i*i#";
    static String[] grammarStr = {"E->E+T", "E->T", "T->T*F", "T->F",
            "F->(E)", "F->i"};//相关文法,(文法中若有空，用'#'输入)
    static Character start = 'E';
    static Character itemStart = 'S';//项目集规范族入口

    static HashMap<Character, ArrayList<String>> production = new HashMap<>();//产生式
    static HashSet<Character> VtSet = new HashSet<>();//终结符集
    static HashSet<Character> VnSet = new HashSet<>();//非终结符
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

    //项目集规范族元素，二元组(项目，展望终结符）
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

    public static void main(String[] args) {
        //初始化
        init();
        //初始项目集I
        HashSet<Item_collection> I = new HashSet<>();
        Item_collection item_collection = new Item_collection(itemStart + "->" + "." + start, '#');
        I.add(item_collection);
        //
        HashSet<Item_collection> set = CLOSURE(I);
        for (Item_collection A : set
        ) {
            System.out.println(A.str + ":" + A.ch);
        }
        collection.add(set);
        HashSet<Item_collection> set1 = GO(0, '#');
        System.out.println("_____________________");
        for (Item_collection A : set1
        ) {
            System.out.println(A.str + ":" + A.ch);
        }
    }

    /**
     * 生成产生式Map(production)，划分终结符（vt）与非终结符（vn），拓展文法S->···
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
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(start + "");
        production.put(itemStart, arrayList);

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
        while (lastNum == curNum) {
            for (Item_collection item : set
            ) {
                String string = item.str;
                int pos = string.indexOf(".");
                //如果'·'不是在最后
                if (pos != string.length()) {

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
                                set.add(a);
                            }

                        }
                    }
                }

            }
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
            int pos = s.indexOf(".");
            if (s.charAt(pos + 1) != X)
                continue;
            Item_collection a = new Item_collection(s.replaceAll("\\." + X, X + "."), item.ch);
            setJ.add(a);
        }
        return CLOSURE(setJ);
    }

    /**
     * 求项目集规范族
     */
    static void getCollection() {
    }

    /**
     * 构造分析表
     */
    static void creatTable() {

    }

    /**
     * 执行LR(1)分析器
     */
    static void startLR_1() {
    }
}
