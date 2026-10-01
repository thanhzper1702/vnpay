package murach.controller;

import com.phamchien.VNPayConfig;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet(name = "PaymentServlet", urlPatterns = {"/payment"})
public class PaymentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String paymentMethod = request.getParameter("paymentMethod");
        String amount = request.getParameter("amount");
        if (amount == null || amount.trim().isEmpty()) {
            amount = "100000"; // Mặc định 100,000 VND
        } else {
            // Loại bỏ dấu phẩy hoặc chấm nếu có
            amount = amount.replace(",", "").replace(".", "").trim();
        }

        // 1. Nếu chọn hình thức Test Simulator (Giả lập tức thì)
        if ("simulator".equalsIgnoreCase(paymentMethod)) {
            String txnRef = "SIM_" + System.currentTimeMillis();
            response.sendRedirect(request.getContextPath() + "/sendEmail?method=simulator&amount=" + amount + "&vnp_TxnRef=" + txnRef);
            return;
        }

        // 2. Nếu chọn hình thức thanh toán qua VNPay Sandbox Gateway
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_OrderInfo = request.getParameter("orderInfo");
        if (vnp_OrderInfo == null || vnp_OrderInfo.trim().isEmpty()) {
            vnp_OrderInfo = "Thanh toan don hang cart";
        }
        String orderType = "topup";
        String vnp_TxnRef = String.valueOf(System.currentTimeMillis());
        String vnp_IpAddr = request.getRemoteAddr();
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

        // Xây dựng vnp_ReturnUrl tự động theo domain/port hiện tại của người dùng
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();

        StringBuilder returnUrlBuilder = new StringBuilder();
        returnUrlBuilder.append(scheme).append("://").append(serverName);
        if ((scheme.equalsIgnoreCase("http") && serverPort != 80) ||
            (scheme.equalsIgnoreCase("https") && serverPort != 443)) {
            returnUrlBuilder.append(":").append(serverPort);
        }
        returnUrlBuilder.append(contextPath).append("/payment-return");
        String vnp_ReturnUrl = returnUrlBuilder.toString();

        long amountVal = 100000;
        try {
            amountVal = Long.parseLong(amount);
        } catch (NumberFormatException e) {
            amountVal = 100000;
        }

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amountVal * 100)); // VNPay tính theo đơn vị hào (x100)
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString())).append('&');
                query.append(fieldName).append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString())).append('&');
            }
        }
        String queryUrl = query.substring(0, query.length() - 1);
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret,
                hashData.substring(0, hashData.length() - 1));
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;
        response.sendRedirect(paymentUrl);
    }
}