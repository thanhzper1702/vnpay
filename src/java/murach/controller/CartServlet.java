package murach.cart;

import murach.data.ProductIO;
import murach.business.LineItem;
import murach.business.Cart;
import murach.business.Product;
import murach.model.User;
import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.util.ArrayList;

public class CartServlet extends HttpServlet {

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

        String url = "/index.jsp";
        ServletContext sc = getServletContext();

        String action = request.getParameter("action");
        if (action == null) {
            action = "cart";
        }

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (action.equals("shop")) {
            url = "/index.jsp";
        } else if (action.equals("checkout")) {
            if (user == null) {
                // Chưa đăng ký / đăng nhập -> chuyển sang trang register
                url = "/register.jsp";
            } else {
                url = "/checkout.jsp";
            }
        } else if (action.equals("add")) {
            String productCode = request.getParameter("productCode");
            String quantityString = request.getParameter("quantity");

            int quantity = 1;
            try {
                if (quantityString != null && !quantityString.trim().isEmpty()) {
                    quantity = Integer.parseInt(quantityString);
                    if (quantity < 1) {
                        quantity = 1;
                    }
                }
            } catch (NumberFormatException nfe) {
                quantity = 1;
            }

            // Nếu người dùng chưa đăng ký / đăng nhập lần đầu
            if (user == null) {
                session.setAttribute("pendingProductCode", productCode);
                session.setAttribute("pendingQuantity", quantity);
                url = "/register.jsp";
                sc.getRequestDispatcher(url).forward(request, response);
                return;
            }

            // Nếu đã đăng nhập -> Thêm sản phẩm vào giỏ hàng
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart == null) {
                cart = new Cart();
            }

            if (productCode != null && !productCode.trim().isEmpty()) {
                String path = sc.getRealPath("/WEB-INF/products.txt");
                Product product = ProductIO.getProduct(productCode, path);

                if (product != null) {
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
                session.setAttribute("cart", cart);
            }

            url = "/cart.jsp";
        } else {
            // action = "cart" (Cập nhật số lượng hoặc xóa sản phẩm)
            String productCode = request.getParameter("productCode");
            String quantityString = request.getParameter("quantity");

            Cart cart = (Cart) session.getAttribute("cart");
            if (cart == null) {
                cart = new Cart();
            }

            if (productCode != null && !productCode.trim().isEmpty()) {
                int quantity = 1;
                try {
                    if (quantityString != null && !quantityString.trim().isEmpty()) {
                        quantity = Integer.parseInt(quantityString);
                    }
                } catch (NumberFormatException nfe) {
                    quantity = 1;
                }

                String path = sc.getRealPath("/WEB-INF/products.txt");
                Product product = ProductIO.getProduct(productCode, path);

                if (product != null) {
                    ArrayList<LineItem> items = cart.getItems();
                    if (items != null) {
                        for (LineItem item : items) {
                            if (item.getProduct() != null &&
                                    item.getProduct().getCode().equalsIgnoreCase(product.getCode())) {
                                if (quantity > 0) {
                                    item.setQuantity(quantity);
                                } else {
                                    cart.removeItem(item);
                                }
                                break;
                            }
                        }
                    }
                }
                session.setAttribute("cart", cart);
            }

            url = "/cart.jsp";
        }

        sc.getRequestDispatcher(url).forward(request, response);
    }
}