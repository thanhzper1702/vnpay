<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>

<head>
  <meta charset="utf-8">
  <title>Murach's Java Servlets and JSP</title>
  <link rel="stylesheet" href="styles/main.css" type="text/css" />
  <style>
    .cart-actions {
      display: flex;
      gap: 10px;
      margin-top: 15px;
    }
    .cart-actions form {
      display: inline;
    }
    .user-greeting {
      margin-bottom: 1em;
      color: #333;
    }
  </style>
</head>

<body>

  <h1>Your cart</h1>

  <c:if test="${not empty sessionScope.user}">
    <p class="user-greeting">Customer: <b>${sessionScope.user.username}</b> (${sessionScope.user.email})</p>
  </c:if>

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
          <th>Quantity</th>
          <th>Description</th>
          <th>Price</th>
          <th>Amount</th>
          <th>&nbsp;</th>
        </tr>

        <c:forEach var="item" items="${cart.items}">
          <tr>
            <td>
              <form action="cart" method="post">
                <input type="hidden" name="action" value="cart">
                <input type="hidden" name="productCode" value="${item.product.code}">
                <input type="text" name="quantity" value="${item.quantity}" id="quantity" size="3">
                <input type="submit" value="Update">
              </form>
            </td>
            <td>${item.product.description}</td>
            <td>${item.product.priceCurrencyFormat}</td>
            <td>${item.totalCurrencyFormat}</td>
            <td>
              <form action="cart" method="post">
                <input type="hidden" name="action" value="cart">
                <input type="hidden" name="productCode" value="${item.product.code}">
                <input type="hidden" name="quantity" value="0">
                <input type="submit" value="Remove Item">
              </form>
            </td>
          </tr>
        </c:forEach>
        <tr>
          <td colspan="3" class="right"><b>Total:</b></td>
          <td><b>${cart.totalCurrencyFormat}</b></td>
          <td>&nbsp;</td>
        </tr>
      </table>

      <p><b>To change the quantity</b>, enter the new quantity and click on the Update button.</p>

      <div class="cart-actions">
        <form action="cart" method="post">
          <input type="hidden" name="action" value="shop">
          <input type="submit" value="Continue Shopping">
        </form>

        <form action="cart" method="post">
          <input type="hidden" name="action" value="checkout">
          <input type="submit" value="Checkout">
        </form>
      </div>
    </c:otherwise>
  </c:choose>

</body>

</html>