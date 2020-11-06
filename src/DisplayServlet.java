import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet(name = "DisplayServlet",urlPatterns = "/display")
public class DisplayServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        super.init();
        LR_1.start();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //分析步骤
        HashMap<String,Object> Info = new HashMap<>();
        Info.put("STACKLIST",LR_1.printlist);
        resp.setHeader("Access-Control-Allow-Origin", "*");
        //ACTION
        HashMap<String,Object> AC = new HashMap<>();
        Vector<HashMap<Character,String>> ac = new Vector<>();
        for (int i = 0; i <LR_1.ACTION.size() ; i++) {
            ac.add(LR_1.ACTION.get(i));
        }
        AC.put("key",LR_1.ACTION.get(0).keySet());
        AC.put("list",ac);
        Info.put("ACTION",AC);
        //GOTO
        HashMap<String,Object> GO = new HashMap<>();
        Vector<HashMap<Character,Integer>> gt = new Vector<>();
        for (int i = 0; i <LR_1.GOTO.size() ; i++) {
            gt.add(LR_1.GOTO.get(i));
        }
        GO.put("key",LR_1.GOTO.get(0).keySet());
        GO.put("list",gt);
        Info.put("GOTO",GO);

        // 调用GSON jar工具包封装好的toJson方法，可直接生成JSON字符串
        Gson gson = new Gson();
        String json = gson.toJson(Info);

        // 输出到界面
        System.out.println(json);
        resp.setContentType("text/json; charset=GBK");
        PrintWriter out = new PrintWriter(resp.getOutputStream());
        out.print(json);
        out.flush();
        // 更多Json转换使用请看JsonTest类

    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

}
