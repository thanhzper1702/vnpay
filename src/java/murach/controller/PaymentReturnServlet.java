package murach.controller;

import com.phamchien.VNPayConfig;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "PaymentReturnServlet", urlPatterns = {"/payment-return"})
public class PaymentReturnServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        Map<String, String> fields = new HashMap<>();
        for (String key : request.getParameterMap().keySet()) {
            String value = request.getParameter(key);
            if (value != null && !value.isEmpty()) {
                fields.put(key, value);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        String signValue = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, VNPayConfig.hashAllFields(fields));

        if (signValue.equalsIgnoreCase(vnp_SecureHash)) {
            String transactionStatus = request.getParameter("vnp_TransactionStatus");
            if ("00".equals(transactionStatus)) {
                // Thanh toán VNPay thành công! Chuyển tiếp sang SendingEmailServlet để gửi mail cảm ơn và trở về trang home
                response.sendRedirect(request.getContextPath() + "/sendEmail?" + request.getQueryString());
            } else {
                // Thanh toán không thành công hoặc người dùng hủy giao dịch
                response.sendRedirect(request.getContextPath() + "/payment_failure.jsp?" + request.getQueryString());
            }
        } else {
            response.getWriter().println("<html><body><h3 style='color:red;'>Lỗi: Chữ ký không hợp lệ!</h3><a href='index.jsp'>Trở về trang chủ</a></body></html>");
        }
    }
}