<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <title>Murach's Java Servlets and JSP</title>
    <link rel="stylesheet" href="styles/main.css" type="text/css" />
    <style>
        .header-bar {
            margin-bottom: 1.5em;
            padding-bottom: 0.8em;
            border-bottom: 1px solid #e0e0e0;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .header-bar a {
            color: teal;
            text-decoration: none;
            font-weight: bold;
        }
        .header-bar a:hover {
            text-decoration: underline;
        }
        .alert-success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
            border-radius: 6px;
            padding: 12px 18px;
            margin-bottom: 20px;
            max-width: 50em;
        }
    </style>
</head>

<body>

    <div class="header-bar">
        <div>
            <c:choose>
                <c:when test="${not empty sessionScope.user}">
                    Xin chào, <b>${sessionScope.user.username}</b> (${sessionScope.user.email})
                </c:when>
                <c:otherwise>
                    Khách vãng lai | <a href="register.jsp">Đăng ký tài khoản</a>
                </c:otherwise>
            </c:choose>
        </div>
        <div>
            <a href="cart">🛒 Xem giỏ hàng (<c:out value="${empty cart ? 0 : cart.count}"/>)</a>
        </div>
    </div>

    <!-- Thông báo kết quả sau khi gửi mail và thanh toán thành công -->
    <c:if test="${not empty sessionScope.orderSuccessMessage}">
        <div class="alert-success">
            <strong>✓ Thông báo:</strong> ${sessionScope.orderSuccessMessage}
        </div>
        <c:remove var="orderSuccessMessage" scope="session"/>
    </c:if>

    <h1>CD list</h1>
    <table>
        <tr>
            <th>Description</th>
            <th class="right">Price</th>
            <th>&nbsp;</th>
        </tr>
        <tr>
            <td>86 (the band) - True Life Songs and Pictures</td>
            <td class="right">$14.95</td>
            <td>
                <form action="cart" method="post">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="productCode" value="8601">
                    <input type="hidden" name="quantity" value="1">
                    <input type="submit" value="Add To Cart">
                </form>
            </td>
        </tr>
        <tr>
            <td>Paddlefoot - The first CD</td>
            <td class="right">$12.95</td>
            <td>
                <form action="cart" method="post">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="productCode" value="pf01">
                    <input type="hidden" name="quantity" value="1">
                    <input type="submit" value="Add To Cart">
                </form>
            </td>
        </tr>
        <tr>
            <td>Paddlefoot - The second CD</td>
            <td class="right">$14.95</td>
            <td>
                <form action="cart" method="post">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="productCode" value="pf02">
                    <input type="hidden" name="quantity" value="1">
                    <input type="submit" value="Add To Cart">
                </form>
            </td>
        </tr>
        <tr>
            <td>Joe Rut - Genuine Wood Grained Finish</td>
            <td class="right">$14.95</td>
            <td>
                <form action="cart" method="post">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="productCode" value="jr01">
                    <input type="hidden" name="quantity" value="1">
                    <input type="submit" value="Add To Cart">
                </form>
            </td>
        </tr>
    </table>

</body>

</html>