//import com.netflix.zuul.ZuulFilter;
//import com.netflix.zuul.context.RequestContext;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CorsFilter extends ZuulFilter {
//
//  @Override
//  public String filterType() {
//    return "pre";
//  }
//
//  @Override
//  public int filterOrder() {
//    return 1;
//  }
//
//  @Override
//  public boolean shouldFilter() {
//    return true;
//  }
//
//  @Override
//  public Object run() {
//    RequestContext ctx = RequestContext.getCurrentContext();
//
//    HttpServletResponse response = ctx.getResponse();
//    response.addHeader("Access-Control-Allow-Origin", "*");
//    response.addHeader("Access-Control-Allow-Methods", "OPTIONS,GET");
//
//    HttpServletRequest request = ctx.getRequest();
//    if (request.getMethod().equals("OPTIONS")) {
//      ctx.setSendZuulResponse(true);
//    }
//
//    return null;
//  }
//}
