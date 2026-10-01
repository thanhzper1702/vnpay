package murach.controller;

import murach.business.Cart;
import murach.business.LineItem;
import murach.business.Product;
import murach.dao.UserDAO;
import murach.data.ProductIO;
import murach.model.User;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "LoginServlet", urlPatterns = {"/register", "/login"})
public class LoginServlet extends HttpServlet {

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

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        if (username == null || username.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập đầy đủ thông tin!");
            getServletContext().getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        // Tạo đối tượng User
        User user = new User(username.trim(), password != null ? password.trim() : "", email.trim());

        // 1. Thao tác insert giả lập qua UserDAO
        UserDAO.insert(user);

        // 2. Lưu user và email vào session
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        session.setAttribute("email", user.getEmail());

        // 3. Tự động thêm sản phẩm đang chờ (nếu có trước khi bấm register)
        String pendingCode = (String) session.getAttribute("pendingProductCode");
        Integer pendingQty = (Integer) session.getAttribute("pendingQuantity");

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
        }

        if (pendingCode != null && !pendingCode.trim().isEmpty()) {
            ServletContext sc = getServletContext();
            String path = sc.getRealPath("/WEB-INF/products.txt");
            Product product = ProductIO.getProduct(pendingCode, path);

            if (product != null) {
                int quantity = (pendingQty != null && pendingQty > 0) ? pendingQty : 1;
                ArrayList<LineItem> items = cart.getItems();
                boolean found = false;

                if (items != null) {
                    for (LineItem item : items) {
                        if (item.getProduct() != null &&
                                item.getProduct().getCode().equalsIgnoreCase(product.getCode())) {
                            found = true;
                            item.increaseQuantity(quantity);
                            break;
                        }
                    }
                }

                if (!found) {
                    LineItem item = new LineItem();
                    item.setProduct(product);
                    item.setQuantity(quantity);
                    cart.addItem(item);
                }
            }
            session.removeAttribute("pendingProductCode");
            session.removeAttribute("pendingQuantity");
        }
        session.setAttribute("cart", cart);

        // 4. Quay lại trang cart để tiếp tục thêm sản phẩm
        response.sendRedirect(request.getContextPath() + "/cart?action=cart");
    }
}
