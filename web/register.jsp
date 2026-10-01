<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <title>User Registration</title>
    <link rel="stylesheet" href="styles/main.css" type="text/css" />
    <style>
        .form-table {
            border: none;
            width: auto;
            margin-bottom: 1em;
        }
        .form-table td {
            border: none;
            padding: 5px 8px;
        }
        .form-table input[type="text"],
        .form-table input[type="password"],
        .form-table input[type="email"] {
            width: 200px;
            padding: 3px;
        }
        .error {
            color: red;
            font-weight: bold;
            margin-bottom: 1em;
        }
        .btn-action {
            margin-top: 5px;
            margin-bottom: 5px;
        }
    </style>
</head>

<body>

    <h1>User Registration</h1>

    <p>To add items to your cart, please enter your name and email address below.</p>

    <c:if test="${not empty errorMessage}">
        <p class="error">${errorMessage}</p>
    </c:if>

    <form action="register" method="post">
        <table class="form-table">
            <tr>
                <td><b>Username:</b></td>
                <td><input type="text" name="username" value="${param.username}" required></td>
            </tr>
            <tr>
                <td><b>Password:</b></td>
                <td><input type="password" name="password" required></td>
            </tr>
            <tr>
                <td><b>Email:</b></td>
                <td><input type="email" name="email" value="${param.email}" required></td>
            </tr>
        </table>

        <div class="btn-action">
            <input type="submit" value="Register & Continue">
        </div>
    </form>

    <div class="btn-action">
        <form action="index.jsp" method="get">
            <input type="submit" value="Back to CD List">
        </form>
    </div>

</body>

</html>
