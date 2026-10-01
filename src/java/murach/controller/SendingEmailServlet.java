package murach.controller;

import murach.business.Cart;
import murach.business.LineItem;
import murach.email.util.MailUtilGmail;
import murach.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet(name = "SendingEmailServlet", urlPatterns = {"/sendEmail", "/sending-email"})
public class SendingEmailServlet extends HttpServlet {

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

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Cart cart = (Cart) session.getAttribute("cart");

        // 1. Xac dinh thong tin nguoi nhan
        String toEmail = null;
        String username = "Qu\u00fd kh\u00e1ch";

        if (user != null) {
            toEmail = user.getEmail();
            if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
                username = user.getUsername();
            }
        }
        if (toEmail == null || toEmail.trim().isEmpty()) {
            toEmail = (String) session.getAttribute("email");
        }
        if (toEmail == null || toEmail.trim().isEmpty()) {
            toEmail = request.getParameter("email");
        }
        if (toEmail == null || toEmail.trim().isEmpty()) {
            toEmail = "thanh17022006@gmail.com";
        }

        // 2. Xac dinh thong tin don hang
        String txnRef = request.getParameter("vnp_TxnRef");
        if (txnRef == null || txnRef.trim().isEmpty()) {
            txnRef = request.getParameter("txnRef");
        }
        if (txnRef == null || txnRef.trim().isEmpty()) {
            txnRef = "TXN" + System.currentTimeMillis();
        }

        long amountVND = 0;
        String vnpAmount = request.getParameter("vnp_Amount");
        if (vnpAmount != null && !vnpAmount.trim().isEmpty()) {
            try {
                amountVND = Long.parseLong(vnpAmount) / 100;
            } catch (NumberFormatException ignored) {
            }
        }
        if (amountVND == 0) {
            String directAmount = request.getParameter("amount");
            if (directAmount != null && !directAmount.trim().isEmpty()) {
                try {
                    amountVND = Long.parseLong(directAmount);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        if (amountVND == 0 && cart != null) {
            amountVND = cart.getTotalVND();
        }

        String paymentMethod = "C\u1ed5ng thanh to\u00e1n VNPay Sandbox";
        if ("simulator".equalsIgnoreCase(request.getParameter("method")) || txnRef.startsWith("SIM_")) {
            paymentMethod = "H\u1ec7 th\u1ed1ng gi\u1ea3 l\u1eadp thanh to\u00e1n t\u1ee9c th\u00ec (Test Simulator)";
        }

        String currentDateStr = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

        // 3. Xay dung Noi dung Van Ban Thuan (Plain Text) don gian, ro rang
        StringBuilder textBody = new StringBuilder();
        textBody.append("Xin ch\u00e0o ").append(username).append(",\n\n");
        textBody.append("C\u1ea3m \u01a1n b\u1ea1n \u0111\u00e3 mua h\u00e0ng t\u1ea1i Ch\u00ed Th\u00e0nh Music Store!\n");
        textBody.append("\u0110\u01a1n h\u00e0ng c\u1ee7a b\u1ea1n \u0111\u00e3 \u0111\u01b0\u1ee3c ghi nh\u1eadn v\u00e0 thanh to\u00e1n th\u00e0nh c\u00f4ng qua h\u1ec7 th\u1ed1ng.\n\n");

        textBody.append("--- TH\u00d4NG TIN \u0110\u01a0N H\u00c0NG ---\n");
        textBody.append("M\u00e3 \u0111\u01a1n h\u00e0ng: #").append(txnRef).append("\n");
        textBody.append("Ph\u01b0\u01a1ng th\u1ee9c thanh to\u00e1n: ").append(paymentMethod).append("\n");
        textBody.append("Th\u1eddi gian giao d\u1ecbch: ").append(currentDateStr).append("\n");
        textBody.append("Tr\u1ea1ng th\u00e1i: \u0110\u00e3 thanh to\u00e1n th\u00e0nh c\u00f4ng\n");
        textBody.append("Email nh\u1eadn th\u00f4ng b\u00e1o: ").append(toEmail).append("\n\n");

        if (cart != null && cart.getItems() != null && !cart.getItems().isEmpty()) {
            textBody.append("--- CHI TI\u1ebeT S\u1ea2N PH\u1ea8M ---\n");
            for (LineItem item : cart.getItems()) {
                textBody.append("+ ")
                        .append(item.getProduct().getDescription())
                        .append(" | SL: ").append(item.getQuantity())
                        .append(" | \u0110\u01a1n gi\u00e1: ").append(item.getProduct().getPriceCurrencyFormat())
                        .append(" | Th\u00e0nh ti\u1ec1n: ").append(item.getTotalCurrencyFormat())
                        .append("\n");
            }
            textBody.append("T\u1ed5ng thanh to\u00e1n (USD): ").append(cart.getTotalCurrencyFormat()).append("\n");
            textBody.append("T\u1ed5ng quy \u0111\u1ed5i (VND): ").append(String.format("%,d", amountVND)).append(" VND\n\n");
        }

        textBody.append("\u0110\u01a1n h\u00e0ng c\u1ee7a b\u1ea1n \u0111ang \u0111\u01b0\u1ee3c \u0111\u00f3ng g\u00f3i v\u00e0 chu\u1ea9n b\u1ecb b\u00e0n giao cho \u0111\u01a1n v\u1ecb v\u1eadn chuy\u1ec3n s\u1edbm nh\u1ea5t.\n");
        textBody.append("N\u1ebfu b\u1ea1n c\u00f3 b\u1ea5t k\u1ef3 th\u1eafc m\u1eafc n\u00e0o, vui l\u00f2ng ph\u1ea3n h\u1ed3i tr\u1ef1c ti\u1ebfp email n\u00e0y ho\u1eb7c li\u00ean h\u1ec7 hotline: 1900 6868.\n\n");
        textBody.append("Tr\u00e2n tr\u1ecdng c\u1ea3m \u01a1n,\n");
        textBody.append("\u0110\u1ed9i ng\u0169 Ch\u00ed Th\u00e0nh Music Store");

        // 4. Tien hanh gui mail van ban thuan (bodyIsHTML = false)
        String fromEmail = "thanh17022006@gmail.com";
        String subject = "[Ch\u00ed Th\u00e0nh Music Store] C\u1ea3m \u01a1n b\u1ea1n \u0111\u00e3 mua h\u00e0ng - \u0110\u01a1n h\u00e0ng #" + txnRef;
        try {
            MailUtilGmail.sendMail(toEmail, fromEmail, subject, textBody.toString(), false);
            System.out.println("[SendingEmailServlet] Gui email plain text cam on thanh cong toi: " + toEmail);
            session.setAttribute("orderSuccessMessage", "Thanh to\u00e1n th\u00e0nh c\u00f4ng! Email c\u1ea3m \u01a1n \u0111\u00e3 \u0111\u01b0\u1ee3c g\u1eedi t\u1edbi " + toEmail + ".");
        } catch (Throwable e) {
            System.err.println("[SendingEmailServlet] Loi gui email qua Brevo: " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("orderSuccessMessage", "Thanh to\u00e1n th\u00e0nh c\u00f4ng! (L\u01b0u \u00fd: Kh\u00f4ng th\u1ec3 g\u1eedi email Brevo: " + e.getMessage() + ")");
        }

        // 5. Xoa gio hang sau khi mua thanh cong
        session.removeAttribute("cart");

        // 6. Tro ve trang home (index.jsp)
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
}
