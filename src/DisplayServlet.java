import com.google.gson.Gson;
//git测试：push成功，测试pull
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "DisplayServlet",urlPatterns = "/display")
public class DisplayServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        Map<String,Object> resMap = new HashMap<>();    // 使用Map存储键值对
        resMap.put("res",1);      // 向Map对象中添加内容
        Map<String,Object> subMap = new HashMap<>();    // 创建子键值对
        subMap.put("name","zhangsan");
        subMap.put("sex","man");
        subMap.put("address","Xi'an");
        resMap.put("userInfo",subMap);  // 将子键值对置入母键值对


        // 调用GSON jar工具包封装好的toJson方法，可直接生成JSON字符串
        Gson gson = new Gson();
        String json = gson.toJson(resMap);

        // 输出到界面
        System.out.println(json);
        resp.setContentType("text/json; charset=utf-8");
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
