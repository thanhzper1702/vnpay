<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:if test="${empty sessionScope.user}">
    <c:redirect url="register.jsp"/>
</c:if>
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <title>CheckOut</title>
    <link rel="stylesheet" href="styles/main.css" type="text/css" />
    <style>
        .customer-card {
            border: 1px solid #c9dada;
            background-color: #fcfefe;
            padding: 12px 18px;
            margin: 1.5em 0;
            max-width: 500px;
        }
        .customer-card h3 {
            color: teal;
            margin-top: 0;
            margin-bottom: 8px;
            font-size: 110%;
        }
        .customer-card p {
            margin: 5px 0;
        }
        .payment-methods {
            margin: 1.2em 0;
            font-size: 14px;
        }
        .payment-methods label {
            margin-right: 20px;
            cursor: pointer;
        }
        .actions-row {
            display: flex;
            gap: 10px;
            margin-top: 10px;
        }
        .actions-row form {
            display: inline;
        }
        .total-display {
            font-size: 115%;
            font-weight: bold;
            margin-top: 1em;
            margin-bottom: 1em;
        }
    </style>
</head>

<body>

    <h1>CheckOut</h1>

    <c:choose>
        <c:when test="${empty cart || empty cart.items || cart.count == 0}">
            <p>Your cart is empty.</p>
            <form action="cart" method="post">
                <input type="hidden" name="action" value="shop">
                <input type="submit" value="Continue Shopping">
            </form>
        </c:when>
        <c:otherwise>
            <table>
                <tr>
                    <th>Description</th>
                    <th>Price</th>
                    <th>Quantity</th>
                    <th>Amount</th>
                </tr>

                <c:forEach var="item" items="${cart.items}">
                    <tr>
                        <td>${item.product.description}</td>
                        <td>${item.product.price}</td>
                        <td>${item.quantity}</td>
                        <td>${item.total}</td>
                    </tr>
                </c:forEach>
            </table>

            <div class="total-display">
                Total: ${cart.totalCurrencyFormat}
                <span style="font-size: 85%; color: #666; font-weight: normal;">
                    (Quy đổi thanh toán: <fmt:formatNumber value="${cart.totalVND}" type="number"/> VND)
                </span>
            </div>

            <!-- Customer Information -->
            <div class="customer-card">
                <h3>Customer Information</h3>
                <p><strong>Username:</strong> ${sessionScope.user.username}</p>
                <p><strong>Email to receive receipt:</strong> ${sessionScope.user.email}</p>
            </div>

            <!-- Payment Selection Form -->
            <form action="payment" method="post" id="paymentForm">
                <input type="hidden" name="orderInfo" value="Thanh toan don hang cart - User: ${sessionScope.user.username}">
                <input type="hidden" name="amount" value="${cart.totalVND}">

                <div class="payment-methods">
                    <strong>Payment Method:</strong> &nbsp;
                    <label>
                        <input type="radio" name="paymentMethod" value="vnpay" checked> VNPay Sandbox Gateway
                    </label>
                    <label>
                        <input type="radio" name="paymentMethod" value="simulator"> Test Simulator (Instant)
                    </label>
                </div>

                <div style="margin-bottom: 15px;">
                    <input type="submit" value="Proceed to Payment">
                </div>
            </form>

            <div class="actions-row">
                <form action="cart" method="post">
                    <input type="hidden" name="action" value="cart">
                    <input type="submit" value="Back to Cart">
                </form>
                <form action="cart" method="post">
                    <input type="hidden" name="action" value="shop">
                    <input type="submit" value="Continue Shopping">
                </form>
            </div>
        </c:otherwise>
    </c:choose>

</body>

</html>