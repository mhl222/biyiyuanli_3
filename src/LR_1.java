import java.util.*;

/**
 *  get JOSN from LL(1) proceessing   for  webServelt
 * @author mhl(Mhlsky)
 *
 */
public class LR_1 {
    static String inStr = "i+i*i#";
    static String[] grammarStr = {"E->E+T" ,"E->T" ,"T->T*F" ,"T->F" ,
                                  "F->(E)" ,"F->i" ,"T->ε"};//相关文法,(文法中若有空，用'ε'输入)
    static HashMap<Character,ArrayList<String>> production = new HashMap<>();//产生式
    static HashSet<Character> VtSet = new HashSet<>();//终结符集
    static HashSet<Character> VnSet = new HashSet<>();//非终结符
    static HashMap<Integer,HashMap<Character,String>> ACTION = new HashMap<>();
    static HashMap<Integer,HashMap<Character,Integer>> GOTO = new HashMap<>();
    //内部类：
    //栈内元素,二元组（状态，文法符号）
    class Item_stack{
        int state;
        Character sign;
    }
    //项目集规范族元素，二元组(项目，展望终结符）
    class Item_collection{
        String str;
        Character ch;
    }
    static ArrayList<ArrayList<Item_collection>> collection = new ArrayList<>();//项目集规范族
    static Stack<Item_stack> stack = new Stack<>();//符号栈
    public static void main(String[] args) {
        dividechar();
       // HashSet<Character> set = getFirst("E+T");
        for (Character ch:VtSet
             ) {
            System.out.println(ch);
        }

    }
    /**
     * 生成产生式Map(production)，划分终结符（vt）与非终结符（vn）
     */
    static void dividechar(){
        //生成产生式Map(production)
        for (String str:grammarStr
        ) {
            //将“|”相连的产生式分开
            String[] strings = str.split("->")[1].split("\\|");
            //非终结符
            char Vch = str.charAt(0);
            ArrayList<String> list = production.containsKey(Vch) ? production.get(Vch) : new ArrayList<String>();
            for (String S:strings
            ) {
                list.add(S);
            }
            production.put(str.charAt(0),list);
            VnSet.add(Vch);
        }
        //寻找终结符
        for (Character ch:VnSet
        ){
            for (String str : production.get(ch)
            ) {
                for (Character c: str.toCharArray()
                ) {
                    if( !VnSet.contains(c) )
                        VtSet.add(c);
                }
            }

        }

    }
    /**
     * 求任意串的first集
     * @param s
     * @return Set
     */
    static HashSet<Character> getFirst(String s){
        HashSet<Character>  set = new HashSet<>();
        int lenth = 0;
        // 从左往右扫描该式
        int i = 0;
        while (i < s.length()) {
            char tn = s.charAt(i);
            HashSet<Character> tvSet = getFirst(tn);
            // 将其非空 first集加入左部
            for (Character tmp : tvSet)
                if(tmp != 'ε')
                    set.add(tmp);
            // 若包含空串 处理下一个符号
            if (tvSet.contains('ε'))
                i++;
                // 否则结束
            else
                break;
            // 到了尾部 即所有符号的first集都包含空串 把空串加入
            if (i == s.length()) {
                set.add('ε');
            }
        }
        return set;
    }
    static HashSet<Character> getFirst(Character ch){
        HashSet<Character> set = new HashSet<>();
        if(VtSet.contains(ch)){
            set.add(ch);
            return set;
        }
        //ch为vn
        for (String str:production.get(ch)
             ) {
            int i = 0;
            while ( i < str.length()){
                char curChar = str.charAt(i);
                HashSet<Character> set1 = getFirst(curChar);
                for (Character tmp: set1
                     ) {
                    if(tmp != 'ε')
                        set.add(tmp);
                }
                if(set1.contains('ε'))
                    i++;
                else
                    break;
            }
            if(i==str.length())
                set.add('ε');
        }
        return set;
    }
    /**
     * 求项目集的闭包
     * @param I 项目集I的下表
     */
    static void CLOSURE( int I){}
    /**
     * 状态转换函数
     * @param I
     * @param X
     */
    static void GO(int I,Character X){}
    /**
     * 求项目集规范族
     */
    static void getCollection(){}
    /**
     * 构造分析表
     */
    static void creatTable(){

    }
    /**
     * 执行LR(1)分析器
     */
    static void startLR_1(){}
}
